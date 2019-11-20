package abeona.behaviours;

import abeona.Query;
import abeona.State;
import abeona.TransitionEvaluationEvent;
import abeona.util.Arguments;

import java.util.function.Predicate;

/**
 * Modifies a query to prevent certain states from ending up in the frontier (being discovered).
 * @param <StateType>
 */
public class FrontierFilterBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final Predicate<StateType> filter;

    /**
     * Creates a discovery filter that requires states to match the given predicate in order to be discovered.
     * @param filter The predicate which identifies what states are allowed.
     * @throws IllegalArgumentException Thrown if the given filter is null
     */
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
