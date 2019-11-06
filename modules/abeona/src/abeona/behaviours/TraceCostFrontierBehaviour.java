package abeona.behaviours;

import abeona.frontiers.DynamicallyOrderedFrontier;
import abeona.Query;
import abeona.State;
import abeona.Transition;
import abeona.util.Arguments;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

public class TraceCostFrontierBehaviour<StateType extends State> extends TraceCostBehaviour<StateType> {
    public TraceCostFrontierBehaviour(ToDoubleFunction<Transition<StateType>> transitionCosts) {
        super(transitionCosts);
    }

    @Override
    public void attach(Query<StateType> query) {
        Arguments.requireInstanceOf(query.getFrontier(), DynamicallyOrderedFrontier.class, "exlorationQuery.getFrontier()");
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
