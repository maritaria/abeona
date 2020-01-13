package abeona.metadata;

import abeona.*;
import abeona.behaviours.AbstractBehaviour;
import abeona.behaviours.SweepLineBehaviour;
import abeona.frontiers.ManagedFrontier;
import abeona.heaps.ManagedHeap;
import abeona.util.Arguments;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Experimental optimization behaviour.
 * This behaviour stores for each state whether it is known directly instead of testing the frontier and heap of a query first.
 * This behaviour modifies the {@link Query#isKnown} interceptor
 * @param <StateType>
 */
public final class IsKnownOptimization<StateType> extends AbstractBehaviour<StateType> {
    private static final Object IS_KNOWN_FLAG = new Object();

    /**
     * Set if a given state is known in a certain query
     * @param query The query under which a state may or may not be known
     * @param state The state to set known-ness for
     * @param isKnown The state of known-ness to apply
     * @throws IllegalArgumentException Thrown if either the query or state are null
     */
    public void setMark(Query<StateType> query, StateType state, boolean isKnown) {
        Arguments.requireNonNull(query, "query");
        Arguments.requireNonNull(state, "state");
        query.getMetadata().set(state, this, isKnown ? IS_KNOWN_FLAG : null);
    }

    /**
     * Gets whether a given state is known in a given query
     * @param query The query under which exploration is occurring
     * @param state The state
     * @return
     */
    public boolean getMark(Query<StateType> query, StateType state) {
        return query.getMetadata().get(state, this).isPresent();
    }

    @Override
    public void attach(Query<StateType> query) {
        this.tapQueryBehaviour(query, query.beforeExploration, this::beforeExploration);
        this.tapQueryBehaviour(query, query.isKnown, this::interceptIsKnown);
        this.tapQueryBehaviour(query, query.onStateDiscovery, this::onDiscovery);
    }

    public void attach(Query<StateType> query, SweepLineBehaviour<StateType> sweepLine) {
        Arguments.requireNonNull(query, "query");
        Arguments.requireNonNull(sweepLine, "sweepLine");
        this.attach(query);
        this.tapQueryBehaviour(query, sweepLine.onPurge, this::onPurge);
    }

    private void beforeExploration(ExplorationEvent<StateType> event) {
        final var query = event.getQuery();
        final Consumer<StateType> markKnown = state -> this.setMark(query, state, true);

        final var frontier = query.getFrontier();
        if (frontier instanceof ManagedFrontier) {
            final var managedFrontier = (ManagedFrontier<StateType>) frontier;
            managedFrontier.forEach(markKnown);
        }

        final var heap = query.getHeap();
        if (heap instanceof ManagedHeap) {
            final var managedHeap = (ManagedHeap<StateType>) heap;
            managedHeap.forEach(markKnown);
        }
    }

    private boolean interceptIsKnown(Query<StateType> query, StateType state, BiFunction<Query<StateType>, StateType, Boolean> next) {
        return getMark(query, state);
    }

    private void onDiscovery(TransitionEvaluationEvent<StateType> event) {
        this.setMark(event.getQuery(), event.getTransition().getTargetState(), true);
    }

    private void onPurge(StateEvent<StateType> event) {
        this.setMark(event.getQuery(), event.getState(), false);
    }
}
