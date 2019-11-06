package abeona.behaviours;

import abeona.Query;
import abeona.State;
import abeona.frontiers.Frontier;
import abeona.frontiers.GeneratorFrontier;
import abeona.frontiers.ManagedFrontier;
import abeona.frontiers.OrderedFrontier;
import abeona.util.Arguments;

import java.util.function.BiFunction;
import java.util.stream.Stream;

public class FrontierCapacityBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final long maximumFrontierSize;

    public FrontierCapacityBehaviour(long maximumFrontierSize) {
        Arguments.requireMinimum(1, maximumFrontierSize, "maximumFrontierSize");
        this.maximumFrontierSize = maximumFrontierSize;
    }

    @Override
    public void attach(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        Arguments.requireInstanceOf(query.getFrontier(), ManagedFrontier.class, "query.frontier");
        Arguments.preventInstanceOf(query.getFrontier(), GeneratorFrontier.class, "query.frontier");
        tapQueryBehaviour(query, query.insertIntoFrontier, this::onFrontierInsert);
    }

    private boolean onFrontierInsert(Frontier<StateType> frontier, Stream<StateType> states, BiFunction<Frontier<StateType>, Stream<StateType>, Boolean> next) {
        assert frontier instanceof ManagedFrontier : "Tapped non-managed frontier type";
        if (frontier instanceof OrderedFrontier) {
            // If the frontier is ordered by a comparator, then use the gladiator system to keep the best states.
            return constrainSortedFrontier((OrderedFrontier<StateType>) frontier, states);
        } else {
            return constrainManagedFrontier((ManagedFrontier<StateType>) frontier, states);
        }
    }

    private boolean constrainSortedFrontier(OrderedFrontier<StateType> frontier, Stream<StateType> states) {
        final var comparator = frontier.comparator();
        final var iterator = states.iterator();
        boolean modified = false;
        while (iterator.hasNext()) {
            final var state = iterator.next();
            if (frontier.size() >= maximumFrontierSize) {
                final var worst = frontier.peekLast().orElseThrow(IllegalStateException::new);
                if (comparator.compare(state, worst) <= 0) {
                    continue;
                }
                frontier.removeLast();
            }
            modified |= frontier.add(state);
        }
        return modified;
    }

    private boolean constrainManagedFrontier(ManagedFrontier<StateType> frontier, Stream<StateType> states) {
        final var iterator = states.iterator();
        boolean modified = false;
        while (frontier.size() < maximumFrontierSize && iterator.hasNext()) {
            modified |= frontier.add(iterator.next());
        }
        return modified;
    }
}
