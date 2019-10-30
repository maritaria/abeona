package abeona.behaviours;

import abeona.ExplorationQuery;
import abeona.State;
import abeona.TransitionEvaluationEvent;
import abeona.util.Arguments;

import java.util.function.Predicate;

public class TerminateOnGoalStateBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final Predicate<StateType> isGoal;

    public TerminateOnGoalStateBehaviour(Predicate<StateType> goalPredicate) {
        Arguments.requireNonNull(goalPredicate, "goalPredicate");
        this.isGoal = goalPredicate;
    }

    @Override
    public void attach(ExplorationQuery<StateType> explorationQuery) {
        Arguments.requireNonNull(explorationQuery, "explorationQuery");
        tapQueryBehaviour(explorationQuery, explorationQuery.onStateDiscovery, this::onDiscovery);
    }

    private void onDiscovery(TransitionEvaluationEvent<StateType> event) {
        final var state = event.getTransition().getTargetState();
        if (isGoal.test(state)) {
            event.abortExploration();
        }
    }
}
