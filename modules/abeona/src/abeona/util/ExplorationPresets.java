package abeona.util;

import abeona.Query;
import abeona.NextFunction;
import abeona.Transition;
import abeona.behaviours.SweepLineBehaviour;
import abeona.behaviours.TraceCostBehaviour;
import abeona.behaviours.TraceCostFrontierBehaviour;
import abeona.behaviours.TraceCostLimitBehaviour;
import abeona.frontiers.QueueFrontier;
import abeona.frontiers.TreeMapFrontier;
import abeona.heaps.HashSetHeap;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

public final class ExplorationPresets {
    public static <StateType> Query<StateType> setupBfs(NextFunction<StateType> neighbours) {
        return new Query<>(QueueFrontier.fifoFrontier(), new HashSetHeap<>(), neighbours);
    }

    public static <StateType> Query<StateType> setupBfsMaxDepth(
            NextFunction<StateType> neighbours,
            int maxDepth
    ) {
        final var query = setupBfs(neighbours);
        final var traceCost = new TraceCostBehaviour<StateType>(unused -> 1);
        final var maxTraceCost = new TraceCostLimitBehaviour<>(traceCost, maxDepth);
        query.addBehaviour(traceCost);
        query.addBehaviour(maxTraceCost);
        return query;
    }

    public static <StateType> Query<StateType> setupDfs(NextFunction<StateType> neighbours) {
        return new Query<>(QueueFrontier.lifoFrontier(), new HashSetHeap<>(), neighbours);
    }

    public static <StateType> Query<StateType> setupDfsMaxDepth(
            NextFunction<StateType> neighbours,
            int maxDepth
    ) {
        final var query = setupDfs(neighbours);
        final var traceCost = new TraceCostBehaviour<StateType>(unused -> 1);
        final var maxTraceCost = new TraceCostLimitBehaviour<>(traceCost, maxDepth);
        query.addBehaviour(traceCost);
        query.addBehaviour(maxTraceCost);
        return query;
    }

    public static <StateType> Query<StateType> setupDijkstra(
            NextFunction<StateType> neighbours,
            ToDoubleFunction<Transition<StateType>> costs
    ) {
        return setupAStar(neighbours, costs, s -> 0);
    }

    public static <StateType> Query<StateType> setupAStar(
            NextFunction<StateType> neighbours,
            ToDoubleFunction<Transition<StateType>> costs,
            ToDoubleFunction<StateType> remainingCostHeuristic
    ) {
        final var traceCost = new TraceCostFrontierBehaviour<>(costs);
        final var comparator = Comparator.<StateType>comparingDouble(state -> traceCost.getTraceCost(state)
                .orElse(0) + remainingCostHeuristic.applyAsDouble(state));
        final var frontier = TreeMapFrontier.withCollisions(comparator, Objects::hashCode);
        final var query = new Query<>(frontier, new HashSetHeap<>(), neighbours);
        query.addBehaviour(traceCost);
        return query;
    }

    public static <StateType> Query<StateType> setupSweepLine(
            NextFunction<StateType> neighbours,
            Comparator<StateType> progressMeasure
    ) {
        final var frontier = TreeMapFrontier.withCollisions(progressMeasure, Objects::hashCode);
        final var heap = new HashSetHeap<StateType>();
        final var query = new Query<>(frontier, heap, neighbours);
        final var sweepLine = new SweepLineBehaviour<>(progressMeasure);
        query.addBehaviour(sweepLine);
        return query;
    }
}
