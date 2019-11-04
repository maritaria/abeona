package abeona.behaviours;

import abeona.*;
import abeona.frontiers.ManagedFrontier;
import abeona.heaps.ManagedHeap;
import abeona.util.Arguments;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class IsKnownOptimization<StateType extends State> extends AbstractBehaviour<StateType> {
    private static final Object IS_KNOWN_FLAG = new Object();

    public void setMark(ExplorationQuery<StateType> query, StateType state, boolean isKnown) {
        query.getMetadata().set(state, this, isKnown ? IS_KNOWN_FLAG : null);
    }

    public boolean getMark(ExplorationQuery<StateType> query, StateType state) {
        return query.getMetadata().get(state, this).isPresent();
    }

    @Override
    public void attach(ExplorationQuery<StateType> query) {
        this.tapQueryBehaviour(query, query.beforeExploration, this::beforeExploration);
        this.tapQueryBehaviour(query, query.isKnown, this::interceptIsKnown);
        this.tapQueryBehaviour(query, query.onStateDiscovery, this::onDiscovery);
    }

    public void attach(ExplorationQuery<StateType> query, SweepLineBehaviour<StateType> sweepLine) {
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

    private boolean interceptIsKnown(ExplorationQuery<StateType> query, StateType state, BiFunction<ExplorationQuery<StateType>, StateType, Boolean> next) {
        return getMark(query, state);
    }

    private void onDiscovery(TransitionEvaluationEvent<StateType> event) {
        this.setMark(event.getQuery(), event.getTransition().getTargetState(), true);
    }

    private void onPurge(StateEvent<StateType> event) {
        this.setMark(event.getQuery(), event.getState(), false);
    }
}
