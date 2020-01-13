package abeona.behaviours;

import abeona.frontiers.DynamicallyOrderedFrontier;
import abeona.Query;
import abeona.Transition;
import abeona.util.Arguments;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

/**
 * Extended version of {@link TraceCostBehaviour} which handles re-sorting the frontiers of querries.
 * Requires the query to use a {@link DynamicallyOrderedFrontier} based frontier to allow for re-sorting on update.
 * @param <StateType>
 */
public class TraceCostFrontierBehaviour<StateType> extends TraceCostBehaviour<StateType> {
    public TraceCostFrontierBehaviour(ToDoubleFunction<Transition<StateType>> transitionCosts) {
        super(transitionCosts);
    }

    /**
     * @param query
     * @throws IllegalArgumentException Thrown if the query is null or the query frontier does not implement {@link DynamicallyOrderedFrontier}.
     */
    @Override
    public void attach(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        Arguments.requireInstanceOf(query.getFrontier(), DynamicallyOrderedFrontier.class, "query.getFrontier()");
        super.attach(query);
    }

    @Override
    public void setTraceCost(StateType state, double cost) {
        final var iterator = this.registeredQueries().iterator();
        if (super.getTraceCost(state).orElse(0) != cost) {
            mutateFrontiers(state, iterator, unused -> super.setTraceCost(state, cost));
        }
    }

    private void mutateFrontiers(StateType state, Iterator<Query<StateType>> queries, Consumer<StateType> proceed) {
        // This method calls mutateOrderedProperty on all registered frontiers to update the mutable property
        // The iterator is passed to recursive invokations of this method such that the callback given to mutateOrderedProperty can block until all frontiers are ready for the mutation.
        // When the iterator has reached its end then the real mutation (the proceed argument) is invoked once after which the recursion ends and is resolved all the way down.
        if (queries.hasNext()) {
            final var query = queries.next();
            final var frontier = (DynamicallyOrderedFrontier<StateType>) query.getFrontier();
            frontier.mutateOrderedProperty(state, s -> mutateFrontiers(s, queries, proceed));
        } else {
            proceed.accept(state);
        }
    }
}
