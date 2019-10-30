package abeona.behaviours;

import abeona.frontiers.DynamicallyOrderedFrontier;
import abeona.ExplorationQuery;
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
    public void attach(ExplorationQuery<StateType> explorationQuery) {
        Arguments.requireInstanceOf(explorationQuery.getFrontier(), DynamicallyOrderedFrontier.class, "exlorationQuery.getFrontier()");
        super.attach(explorationQuery);
    }

    @Override
    public void setTraceCost(StateType state, double cost) {
        final var iterator = this.registeredQueries().iterator();
        mutateFrontiers(state, iterator, unused -> super.setTraceCost(state, cost));
    }

    private void mutateFrontiers(StateType state, Iterator<ExplorationQuery<StateType>> queries, Consumer<StateType> proceed) {
        // This method calls mutateOrderedProperty on all registered frontiers to update the mutable property
        // The iterator is passed to recursive invokations of this method such that the callback given to mutateOrderedProperty can block until all frontiers are ready for the mutation.
        // When the iterator has reached its end then the real mutation (the proceed argument) is invoked once after which the recursion ends and is resolved all the way down.
        if (queries.hasNext()) {
            final var explorationQuery = queries.next();
            final var frontier = (DynamicallyOrderedFrontier<StateType>) explorationQuery.getFrontier();
            frontier.mutateOrderedProperty(state, s -> mutateFrontiers(s, queries, proceed));
        } else {
            proceed.accept(state);
        }
    }
}
