package abeona.demos.staircase;

import abeona.ExplorationQuery;
import abeona.StateEvaluationEvent;
import abeona.Transition;
import abeona.TransitionEvaluationEvent;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.frontiers.QueueFrontier;
import abeona.frontiers.TreeMapFrontier;
import abeona.heaps.HashSetHeap;
import abeona.util.StateWrapper;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.stream.Stream;

class StaircaseTest {
    @Test
    void solveWithBfs() {
        final var staircase = new Staircase(10, 1, 2, 3);
        final var query = new ExplorationQuery<StateWrapper<Integer>>(QueueFrontier.fifoFrontier(),
                new HashSetHeap<>(),
                state -> staircase.next(state.unwrap())
                        .map(StateWrapper::new)
                        .map(target -> new Transition<>(state, target)));
        query.getFrontier().add(Stream.of(new StateWrapper<>(0)));
        query.onStateDiscovery.tap(this::onDiscovery);
        query.explore();
    }

    @Test
    void solveWithSortedFrontier() {
        final var staircase = new Staircase(10, 1, 4);
        final var comp = Comparator.<StateWrapper<Integer>>comparingInt(StateWrapper::unwrap).reversed();
        final var query = new ExplorationQuery<>(TreeMapFrontier.withExactOrdering(comp),
                new HashSetHeap<>(),
                state -> staircase.next(state.unwrap())
                        .map(StateWrapper::new)
                        .map(target -> new Transition<>(state, target)));
        query.getFrontier().add(Stream.of(new StateWrapper<>(0)));
        query.beforeStateEvaluation.tap(this::logEvaluation);
        query.onStateDiscovery.tap(this::onDiscovery);
        final var goalBehaviour = new TerminateOnGoalStateBehaviour<StateWrapper<Integer>>(wrapper -> staircase.isGoal(
                wrapper.unwrap()));
        goalBehaviour.attach(query);
        goalBehaviour.onGoal.tap(this::onGoal);
        query.explore();
    }

    private void onGoal(TransitionEvaluationEvent<StateWrapper<Integer>> event) {
        System.out.println("Found goal state: " + event.getTransition().getTargetState());
    }

    private void logEvaluation(StateEvaluationEvent<StateWrapper<Integer>> event) {
        System.out.println();
        System.out.println("Evaluating state: " + event.getSourceState());
    }

    private void onDiscovery(TransitionEvaluationEvent<StateWrapper<Integer>> event) {
        System.out.println("Discovered: " + event.getTransition().getTargetState());
    }
}