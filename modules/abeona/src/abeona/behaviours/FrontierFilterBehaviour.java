package abeona.behaviours;

import abeona.Query;
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
    public void attach(Query<StateType> query) {
        tapQueryBehaviour(query, query.onTransitionEvaluation, this::onTransitionEvaluation);
    }

    private <T> void onTransitionEvaluation(TransitionEvaluationEvent<StateType> event) {
        event.filterTargetState(filter);
    }
}
