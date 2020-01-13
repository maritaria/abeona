package abeona.behaviours;

import abeona.Query;
import abeona.TransitionEvaluationEvent;
import abeona.aspects.EventTap;
import abeona.util.Arguments;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * This behaviour terminates queries when they find a state matching a given (goal) predicate.
 * Additionally the behaviour exposes a {@link #onGoal} tappable event for handling the discovery of goal states.
 * @param <StateType>
 */
public final class TerminateOnGoalStateBehaviour<StateType> extends AbstractBehaviour<StateType> {
    private final Predicate<StateType> isGoal;
    /**
     * Tappable event, fires when a state is discovered that matches the goal state predicate.
     */
    public final EventTap<TransitionEvaluationEvent<StateType>> onGoal = new EventTap<>();

    /**
     * Sets up the behaviour to terminate queries when they discover a state matching the given predicate
     * @param goalPredicate A predicate that identifies goal states.
     * @throws IllegalArgumentException Thrown if the given predicate is null
     */
    public TerminateOnGoalStateBehaviour(Predicate<StateType> goalPredicate) {
        Arguments.requireNonNull(goalPredicate, "goalPredicate");
        this.isGoal = goalPredicate;
    }

    @Override
    public void attach(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        tapQueryBehaviour(query, query.onStateDiscovery, this::onDiscovery);
    }

    private void onDiscovery(TransitionEvaluationEvent<StateType> event) {
        final var state = event.getTransition().getTargetState();
        if (isGoal.test(state)) {
            onGoal.accept(event);
            event.abortExploration();
        }
    }

    /**
     * Helper function which runs a given query and returns the goal state if found.
     * @param query The query to run exploration for
     * @return An optional holding the found goal state or empty if exploration terminated without discovering a goal state.
     * @throws IllegalArgumentException Thrown if the query is null or this behaviour is not registered on the query.
     */
    public Optional<StateType> wrapExploration(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        if (!this.hasRegisteredTo(query)) {
            throw new IllegalArgumentException("The behaviour is not registered for that query");
        }
        AtomicReference<StateType> result = new AtomicReference<>(null);
        final var handler = onGoal.tap(goal -> result.set(goal.getTransition().getTargetState()));
        try {
            query.explore();
            return Optional.ofNullable(result.get());
        } finally {
            onGoal.unTap(handler);
        }
    }
}
