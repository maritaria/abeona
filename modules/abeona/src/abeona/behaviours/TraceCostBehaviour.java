package abeona.behaviours;

import abeona.*;
import abeona.aspects.EventTap;
import abeona.util.Arguments;

import java.util.*;
import java.util.function.ToDoubleFunction;

/**
 * This behaviour tracks the double-based cost to reach a given state from the initial states of a query.
 * This behaviour automatically updates the cost of a state when a cheaper path is found, but this update does not propagate to neighbours.
 * @param <StateType>
 */
public class TraceCostBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final ToDoubleFunction<Transition<StateType>> transitionCosts;
    private final Map<StateType, Double> costMetadata = new WeakHashMap<>();
    /**
     * Tappable event fired when a transition is found that lowers the cost of a state.
     * This is not fired when finding the cost for a unknown state.
     */
    public final EventTap<TransitionEvaluationEvent<StateType>> onLowerPathFound = new EventTap<>();

    /**
     * Sets up the behaviour to derive cost of transitions based on a given function
     * @param transitionCosts The function that indicates the cost of a given transition
     * @throws IllegalArgumentException Thrown if the function is null
     */
    public TraceCostBehaviour(ToDoubleFunction<Transition<StateType>> transitionCosts) {
        Arguments.requireNonNull(transitionCosts, "transitionCosts");
        this.transitionCosts = transitionCosts;
    }

    @Override
    public void attach(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        tapQueryBehaviour(query, query.onTransitionEvaluation, this::onTransitionEvaluation);
    }

    private void onTransitionEvaluation(TransitionEvaluationEvent<StateType> event) {
        final var transition = event.getTransition();
        final double transitionCost = transitionCosts.applyAsDouble(transition);
        final var source = transition.getSourceState();
        final var target = transition.getTargetState();
        final double currentCost = getTraceCost(source).orElse(0) + transitionCost;
        getTraceCost(target).ifPresentOrElse(existingCost -> {
            if (currentCost < existingCost) {
                setTraceCost(target, currentCost);
                onLowerPathFound.accept(event);
            }
        }, () -> setTraceCost(target, currentCost));
    }


    /**
     * Gets the known cost to reach a given state from the initial states.
     * Empty if the cost is not known or if the state is an initial state.
     * @param state
     * @return
     * @throws IllegalArgumentException Thrown if the given state is null
     */
    public OptionalDouble getTraceCost(StateType state) {
        Arguments.requireNonNull(state, "state");
        final var cost = costMetadata.get(state);
        return cost == null ? OptionalDouble.empty() : OptionalDouble.of(cost);
    }

    /**
     * Sets the cost to reach a given state. Does not trigger {@link #onLowerPathFound}.
     * @param state
     * @param cost
     * @throws IllegalArgumentException Thrown if the given state is null
     */
    public void setTraceCost(StateType state, double cost) {
        Arguments.requireNonNull(state, "state");
        Arguments.requireNonNull(state, "state");
        costMetadata.put(state, cost);
    }
}
