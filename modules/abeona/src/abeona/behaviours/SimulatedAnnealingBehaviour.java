package abeona.behaviours;

import abeona.Query;
import abeona.State;
import abeona.aspects.BiFunctionTap;
import abeona.frontiers.Frontier;
import abeona.util.Arguments;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Realizes a simulated annealing algorithm flow in a query.
 * Taps {@link Query#insertIntoFrontier} and {@link Query#afterStatePicked}.
 *
 * This variation of simulated annealing works by keeping a single state in the frontier
 * The state in the frontier is selected based on a chance to transition from the current state to the new state
 * Each state evaluation lowers the temperature a little.
 * If no state is accepted the current state is re-inserted into the frontier.
 *
 * @param <StateType>
 */
public class SimulatedAnnealingBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final double startingTemperature;
    private final double settleSpeed;
    private final long randomSeed;
    private final ToDoubleFunction<StateType> energy;
    private final TransitionChance probability;
    private final Map<Query<StateType>, InsertIntoFrontier> interceptors = new WeakHashMap<>(1);

    public SimulatedAnnealingBehaviour(
            double startingTemperature,
            double settleSpeed,
            long randomSeed,
            ToDoubleFunction<StateType> energy,
            TransitionChance probability
    ) {
        Arguments.requireMinimum(Double.MIN_VALUE, settleSpeed, "settleSpeed");
        Arguments.requireNonNull(energy, "energy");
        Arguments.requireNonNull(probability, "probability");
        this.startingTemperature = startingTemperature;
        this.settleSpeed = settleSpeed;
        this.randomSeed = randomSeed;
        this.energy = energy;
        this.probability = probability;
    }

    @Override
    public void attach(Query<StateType> query) {
        final var interceptor = new InsertIntoFrontier(query);
        tapQueryBehaviour(query, query.insertIntoFrontier, interceptor);
        tapQueryBehaviour(
                query,
                query.afterStatePicked,
                event -> interceptor.setCurrent(event.getState()));
        interceptors.put(query, interceptor);
    }

    public void resetTemperature(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        final var interceptor = interceptors.get(query);
        if (interceptor != null) {
            interceptor.resetTemperature();
        }
    }

    public OptionalDouble getTemperature(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        final var interceptor = interceptors.get(query);
        if (interceptor != null) {
            return OptionalDouble.of(interceptor.temperature);
        } else {
            return OptionalDouble.empty();
        }
    }

    private final class InsertIntoFrontier implements BiFunctionTap.Interceptor<Frontier<StateType>, Stream<StateType>, Boolean> {
        private final Query<StateType> query;
        private final Random random = new Random(randomSeed);
        private double progress = 0;
        private double temperature = startingTemperature;
        private StateType current = null; // Updated through Query.afterStatePicked
        private double currentEnergy = 0;

        InsertIntoFrontier(Query<StateType> query) {
            this.query = query;
        }

        void setCurrent(StateType state) {
            this.current = state;
            this.currentEnergy = energy.applyAsDouble(state);
        }

        @Override
        public Boolean intercept(
                Frontier<StateType> frontier,
                Stream<StateType> states,
                BiFunction<Frontier<StateType>, Stream<StateType>, Boolean> next
        ) {
            if (current == null) {
                return next.apply(frontier, states);
            }
            try {
                // Empty the frontier
                frontier.clear();
                final var nextState = findNext(states);
                return nextState != null && frontier.add(Stream.of(nextState));
            } finally {
                updateTemperature();
            }
        }

        private StateType findNext(Stream<StateType> states) {
            // Collect the neighbours
            final var items = states.collect(Collectors.toList());
            final int itemsCount = items.size();
            // If no neighbours then simulation is done
            if (itemsCount == 0) {
                return null;
            }
            // Pick the next state
            while (items.size() > 0) {
                final int randomIndex = random.nextInt(items.size());
                final var item = items.remove(randomIndex);
                if (shouldInsert(item)) {
                    return item;
                }
            }
            return current;
        }

        private boolean shouldInsert(StateType next) {
            final var itemEnergy = energy.applyAsDouble(next);
            final var chance = probability.get(currentEnergy, itemEnergy, temperature);
            if (chance >= 1) {
                return true;
            } else {
                final double rolled = random.nextDouble();
                return rolled < chance;
            }
        }

        void updateTemperature() {
            progress += settleSpeed;
            temperature = startingTemperature / progress;
        }

        void resetTemperature() {
            progress = 0;
            temperature = startingTemperature;
        }
    }

    @FunctionalInterface
    public interface TransitionChance {
        double get(double currentEnergy, double nextEnergy, double temperature);

        static TransitionChance standard() {
            return (currentEnergy, nextEnergy, temperature) -> {
                if (currentEnergy > nextEnergy) {
                    return 1;
                } else {
                    final double energyDelta = nextEnergy - currentEnergy;
                    return Math.exp(-energyDelta / temperature);
                }
            };
        }
    }
}
