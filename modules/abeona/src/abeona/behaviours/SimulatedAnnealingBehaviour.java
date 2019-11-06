package abeona.behaviours;

import abeona.ExplorationQuery;
import abeona.State;
import abeona.aspects.BiFunctionTap;
import abeona.frontiers.Frontier;
import abeona.util.Arguments;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimulatedAnnealingBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final double startingTemperature;
    private final double settleSpeed;
    private final long randomSeed;
    private final ToDoubleFunction<StateType> energy;
    private final Map<ExplorationQuery<StateType>, InsertIntoFrontier> interceptors = new WeakHashMap<>(1);
    public int randomBound = 1000;

    public SimulatedAnnealingBehaviour(
            double startingTemperature, double settleSpeed, long randomSeed, ToDoubleFunction<StateType> energy
    ) {
        Arguments.requireMinimum(Double.MIN_VALUE, settleSpeed, "settleSpeed");
        Arguments.requireNonNull(energy, "energy");
        this.startingTemperature = startingTemperature;
        this.settleSpeed = settleSpeed;
        this.randomSeed = randomSeed;
        this.energy = energy;
    }

    @Override
    public void attach(ExplorationQuery<StateType> explorationQuery) {
        final var interceptor = new InsertIntoFrontier(explorationQuery);
        tapQueryBehaviour(explorationQuery, explorationQuery.insertIntoFrontier, interceptor);
        tapQueryBehaviour(explorationQuery,
                explorationQuery.afterStatePicked,
                event -> interceptor.setCurrent(event.getState()));
        interceptors.put(explorationQuery, interceptor);
    }

    public void resetTemperature(ExplorationQuery<StateType> query) {
        Arguments.requireNonNull(query, "query");
        final var interceptor = interceptors.get(query);
        if (interceptor != null) {
            interceptor.resetTemperature();
        }
    }

    public OptionalDouble getTemperature(ExplorationQuery<StateType> query) {
        Arguments.requireNonNull(query, "query");
        final var interceptor = interceptors.get(query);
        if (interceptor != null) {
            return OptionalDouble.of(interceptor.temperature);
        } else {
            return OptionalDouble.empty();
        }
    }

    private final class InsertIntoFrontier implements BiFunctionTap.Interceptor<Frontier<StateType>, Stream<StateType>, Boolean> {
        private final ExplorationQuery<StateType> query;
        private final Random random = new Random(randomSeed);
        private double progress = 0;
        private double temperature = startingTemperature;
        private StateType current = null; // Updated through ExplorationQuery.afterStatePicked
        private double currentEnergy = 0;

        InsertIntoFrontier(ExplorationQuery<StateType> query) {
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
                return nextState == null ? false : frontier.add(Stream.of(nextState));
            } finally {
                updateTemperature();
            }
        }


        private StateType findNext(Stream<StateType> states) {
            final var iterator = states.iterator();
            if (!iterator.hasNext()) {
                return null;
            }
            final var collected = new ArrayList<StateType>(randomBound);
            collected.add(iterator.next());
            int collectedCount = 1;
            while (iterator.hasNext() || !collected.isEmpty()) {
                int index = random.nextInt(randomBound);
                if (index >= collectedCount) {
                    for (int i = 0; i < index; i++) {
                        if (iterator.hasNext()) {
                            collected.add(iterator.next());
                            collectedCount++;
                        } else {
                            index %= collectedCount;
                            break;
                        }
                    }
                }
                collectedCount--;
                final var item = collected.remove(index);
                if (shouldInsert(item)) {
                    return item;
                }
            }
            return current;
        }

        private StateType findNext2(Stream<StateType> states) {
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
            if (itemEnergy < currentEnergy) {
                return true;
            } else {
                final double energyDelta = itemEnergy - currentEnergy;
                final double chance = Math.exp(-energyDelta / temperature);
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

    private static final class AnnealingState {
        double temperature;

        AnnealingState(double temperature) {
            this.temperature = temperature;
        }
    }
}
