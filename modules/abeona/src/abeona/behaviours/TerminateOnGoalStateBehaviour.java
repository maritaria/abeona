package abeona.behaviours;

import abeona.ExplorationQuery;
import abeona.State;
import abeona.TransitionEvaluationEvent;
import abeona.aspects.EventTap;
import abeona.util.Arguments;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public final class TerminateOnGoalStateBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final Predicate<StateType> isGoal;
    public final EventTap<TransitionEvaluationEvent<StateType>> onGoal = new EventTap<>();

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
            onGoal.accept(event);
            event.abortExploration();
        }
    }

    public Optional<StateType> wrapExploration(ExplorationQuery<StateType> query) {
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
