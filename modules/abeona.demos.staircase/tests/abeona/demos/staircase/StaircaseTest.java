package abeona.demos.staircase;

import abeona.Query;
import abeona.StateEvaluationEvent;
import abeona.Transition;
import abeona.TransitionEvaluationEvent;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.frontiers.QueueFrontier;
import abeona.frontiers.TreeMapFrontier;
import abeona.heaps.HashSetHeap;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.stream.Stream;

class StaircaseTest {
    @Test
    void solveWithBfs() {
        final var staircase = new Staircase(10, 1, 2, 3);
        final var query = new Query<Integer>(QueueFrontier.fifoFrontier(),
                new HashSetHeap<>(),
                state -> staircase.next(state)
                        .map(target -> new Transition<>(state, target)));
        query.getFrontier().add(Stream.of(0));
        query.onStateDiscovery.tap(this::onDiscovery);
        query.explore();
    }

    @Test
    void solveWithSortedFrontier() {
        final var staircase = new Staircase(10, 1, 4);
        final var comp = Comparator.comparingInt(Integer::intValue).reversed();
        final var query = new Query<>(TreeMapFrontier.withExactOrdering(comp),
                new HashSetHeap<>(),
                state -> staircase.next(state)
                        .map(target -> new Transition<>(state, target)));
        query.getFrontier().add(Stream.of(0));
        query.beforeStateEvaluation.tap(this::logEvaluation);
        query.onStateDiscovery.tap(this::onDiscovery);
        final var goalBehaviour = new TerminateOnGoalStateBehaviour<Integer>(staircase::isGoal);
        goalBehaviour.attach(query);
        goalBehaviour.onGoal.tap(this::onGoal);
        query.explore();
    }

    private void onGoal(TransitionEvaluationEvent<Integer> event) {
        System.out.println("Found goal state: " + event.getTransition().getTargetState());
    }

    private void logEvaluation(StateEvaluationEvent<Integer> event) {
        System.out.println();
        System.out.println("Evaluating state: " + event.getSourceState());
    }

    private void onDiscovery(TransitionEvaluationEvent<Integer> event) {
        System.out.println("Discovered: " + event.getTransition().getTargetState());
    }
}