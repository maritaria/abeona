package abeona;

import abeona.util.Arguments;

import java.util.function.Predicate;

public final class TransitionEvaluationEvent<StateType extends State> extends ExplorationEvent<StateType> {
    private final Transition<StateType> transition;
    private boolean saveTargetState = true;

    public boolean getSaveTargetState() {
        return saveTargetState;
    }

    public void setSaveTargetState(boolean value) {
        saveTargetState = value;
    }

    public void filterTargetState(Predicate<StateType> predicate) {
        if (saveTargetState && !predicate.test(transition.getTargetState())) {
            saveTargetState = false;
        }
    }

    public Transition<StateType> getTransition() {
        return transition;
    }

    public TransitionEvaluationEvent(Query<StateType> query, Transition<StateType> transition) {
        super(query);
        Arguments.requireNonNull(transition, "transition");
        this.transition = transition;
    }
}
