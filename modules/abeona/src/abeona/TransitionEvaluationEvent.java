package abeona;

import abeona.util.Arguments;

import java.util.function.Predicate;

/**
 * The event fired when transitions are being evaluated.
 * @param <StateType>
 */
public final class TransitionEvaluationEvent<StateType> extends ExplorationEvent<StateType> {
    private final Transition<StateType> transition;
    private boolean saveTargetState = true;

    public boolean getSaveTargetState() {
        return saveTargetState;
    }

    /**
     * Set whether the state is a new discovery and should end up in the heap.
     * If this is set to false then the {@link Query#onStateDiscovery} tap does not fire for with this transition.
     * This overwrites any other descision made to keep/discard the state.
     */
    public void setSaveTargetState(boolean value) {
        saveTargetState = value;
    }

    /**
     * Adds a condition to retain the target state of the transition.
     * The predicate is only evaluated if the target state is set as a discovery, otherwise the predicate is not executed.
     * @param predicate
     */
    public void filterTargetState(Predicate<StateType> predicate) {
        if (saveTargetState && !predicate.test(transition.getTargetState())) {
            saveTargetState = false;
        }
    }

    /**
     * Gets the transition the event is related to
     * @return
     */
    public Transition<StateType> getTransition() {
        return transition;
    }

    /**
     * @param query The query which is evaluating the transition.
     * @param transition The transition being evaluated.
     * @throws IllegalArgumentException Thrown if any argument is null.
     */
    public TransitionEvaluationEvent(Query<StateType> query, Transition<StateType> transition) {
        super(query);
        Arguments.requireNonNull(transition, "transition");
        this.transition = transition;
    }
}
