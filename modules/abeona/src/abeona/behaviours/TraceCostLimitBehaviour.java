package abeona.behaviours;

import abeona.ExplorationQuery;
import abeona.State;
import abeona.TransitionEvaluationEvent;
import abeona.util.Arguments;

public class TraceCostLimitBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final TraceCostBehaviour<StateType> traceCostBehaviour;
    private double maximumCost;

    public TraceCostLimitBehaviour(TraceCostBehaviour<StateType> traceCostBehaviour, double maximumCost) {
        Arguments.requireNonNull(traceCostBehaviour, "traceCostBehaviour");
        this.traceCostBehaviour = traceCostBehaviour;
        this.maximumCost = maximumCost;
    }

    @Override
    public void attach(ExplorationQuery<StateType> explorationQuery) {
        traceCostBehaviour.attach(explorationQuery);
        tapQueryBehaviour(explorationQuery, explorationQuery.onTransitionEvaluation, this::onTransitionEvaluation);
    }

    private <T> void onTransitionEvaluation(TransitionEvaluationEvent<StateType> event) {
        event.filterTargetState(state -> traceCostBehaviour.getTraceCost(state).orElse(maximumCost) <= maximumCost);
    }
}
