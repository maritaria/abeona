package abeona.behaviours;

import abeona.Query;
import abeona.State;
import abeona.TransitionEvaluationEvent;
import abeona.util.Arguments;

/**
 * Limits a query from exploring states with costs above a given threshold.
 * Requires a {@link TraceCostBehaviour} to define the cost measure.
 * @param <StateType>
 */
public class TraceCostLimitBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final TraceCostBehaviour<StateType> traceCostBehaviour;
    private double maximumCost;

    /**
     * Creates a cost-limit behaviour based on an existing trace cost behaviour and a maximum cost limit
     * @param traceCostBehaviour The behaviour that defines the transition-cost function
     * @param maximumCost The maximum (inclusive) cost a state may have to allow it to be evaluated.
     * @throws IllegalArgumentException Thrown if the given trace cost behaviour is null
     */
    public TraceCostLimitBehaviour(TraceCostBehaviour<StateType> traceCostBehaviour, double maximumCost) {
        Arguments.requireNonNull(traceCostBehaviour, "traceCostBehaviour");
        this.traceCostBehaviour = traceCostBehaviour;
        this.maximumCost = maximumCost;
    }

    @Override
    public void attach(Query<StateType> query) {
        traceCostBehaviour.attach(query);
        tapQueryBehaviour(query, query.onTransitionEvaluation, this::onTransitionEvaluation);
    }

    @Override
    public void detach(Query<StateType> query) {
        super.detach(query);
        traceCostBehaviour.detach(query);
    }

    private <T> void onTransitionEvaluation(TransitionEvaluationEvent<StateType> event) {
        event.filterTargetState(state -> traceCostBehaviour.getTraceCost(state).orElse(maximumCost) <= maximumCost);
    }
}
