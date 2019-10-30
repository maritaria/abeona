package abeona.behaviours;

import abeona.ExplorationQuery;
import abeona.State;
import abeona.TransitionEvaluationEvent;
import abeona.util.Arguments;

import java.util.function.Predicate;

public class FrontierFilterBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final Predicate<StateType> filter;

    public FrontierFilterBehaviour(Predicate<StateType> filter) {
        Arguments.requireNonNull(filter, "filter");
        this.filter = filter;
    }

    @Override
    public void attach(ExplorationQuery<StateType> explorationQuery) {
        tapQueryBehaviour(explorationQuery, explorationQuery.onTransitionEvaluation, this::onTransitionEvaluation);
    }

    private <T> void onTransitionEvaluation(TransitionEvaluationEvent<StateType> event) {
        event.filterTargetState(filter);
    }
}
