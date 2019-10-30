package abeona.behaviours;

import abeona.*;
import abeona.aspects.EventTap;
import abeona.util.Arguments;

import java.util.OptionalDouble;
import java.util.WeakHashMap;
import java.util.function.ToDoubleFunction;

public class TraceCostBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final ToDoubleFunction<Transition<StateType>> transitionCosts;
    public final EventTap<TransitionEvaluationEvent<StateType>> onLowerPathFound = new EventTap<>();

    public TraceCostBehaviour(ToDoubleFunction<Transition<StateType>> transitionCosts) {
        Arguments.requireNonNull(transitionCosts, "transitionCosts");
        this.transitionCosts = transitionCosts;
    }

    @Override
    public void attach(ExplorationQuery<StateType> explorationQuery) {
        Arguments.requireNonNull(explorationQuery, "explorationQuery");
        tapQueryBehaviour(explorationQuery, explorationQuery.onTransitionEvaluation, this::onTransitionEvaluation);
    }

    private void onTransitionEvaluation(TransitionEvaluationEvent<StateType> event) {
        final var transition = event.getTransition();
        final double transitionCost = transitionCosts.applyAsDouble(transition);
        final var source = transition.getSourceState();
        final var target = transition.getTargetState();

        double currentCost = getTraceCost(source).orElse(0) + transitionCost;
        getTraceCost(target).ifPresentOrElse(existingCost -> {
            if (currentCost < existingCost) {
                setTraceCost(target, currentCost);
                onLowerPathFound.accept(event);
            }
        }, () -> setTraceCost(target, currentCost));
    }

    private final WeakHashMap<StateType, Double> costMetadata = new WeakHashMap<>();

    public OptionalDouble getTraceCost(StateType state) {
        Arguments.requireNonNull(state, "state");
        return costMetadata.containsKey(state) ? OptionalDouble.of(costMetadata.get(state)) : OptionalDouble.empty();
    }

    public void setTraceCost(StateType state, double cost) {
        Arguments.requireNonNull(state, "state");
        costMetadata.put(state, cost);
    }
}
