package abeona.util;

import abeona.ExplorationQuery;
import abeona.State;
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
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

public final class ExplorationPresets {
    public static <StateType extends State> ExplorationQuery<StateType> setupBfs(Function<StateType, Stream<Transition<StateType>>> neighbours) {
        return new ExplorationQuery<>(QueueFrontier.fifoFrontier(), new HashSetHeap<>(), neighbours);
    }

    public static <StateType extends State> ExplorationQuery<StateType> setupBfsMaxDepth(Function<StateType, Stream<Transition<StateType>>> neighbours, int maxDepth) {
        final var query = setupBfs(neighbours);
        final var traceCost = new TraceCostBehaviour<StateType>(unused -> 1);
        final var maxTraceCost = new TraceCostLimitBehaviour<>(traceCost, maxDepth);
        query.addBehaviour(traceCost);
        query.addBehaviour(maxTraceCost);
        return query;
    }

    public static <StateType extends State> ExplorationQuery<StateType> setupDfs(Function<StateType, Stream<Transition<StateType>>> neighbours) {
        return new ExplorationQuery<>(QueueFrontier.lifoFrontier(), new HashSetHeap<>(), neighbours);
    }

    public static <StateType extends State> ExplorationQuery<StateType> setupDfsMaxDepth(Function<StateType, Stream<Transition<StateType>>> neighbours, int maxDepth) {
        final var query = setupDfs(neighbours);
        final var traceCost = new TraceCostBehaviour<StateType>(unused -> 1);
        final var maxTraceCost = new TraceCostLimitBehaviour<>(traceCost, maxDepth);
        query.addBehaviour(traceCost);
        query.addBehaviour(maxTraceCost);
        return query;
    }

    public static <StateType extends State> ExplorationQuery<StateType> setupDijkstra(Function<StateType, Stream<Transition<StateType>>> neighbours, ToDoubleFunction<Transition<StateType>> costs) {
        return setupAStar(neighbours, costs, s -> 0);
    }

    public static <StateType extends State> ExplorationQuery<StateType> setupAStar(Function<StateType, Stream<Transition<StateType>>> neighbours, ToDoubleFunction<Transition<StateType>> costs, ToDoubleFunction<StateType> remainingCostHeuristic) {
        final var traceCost = new TraceCostFrontierBehaviour<>(costs);
        final var comparator = Comparator.<StateType>comparingDouble(state -> traceCost.getTraceCost(state).orElseThrow() + remainingCostHeuristic.applyAsDouble(state));
        final var frontier = TreeMapFrontier.withCollisions(comparator, Objects::hashCode);
        final var query = new ExplorationQuery<>(frontier, new HashSetHeap<>(), neighbours);
        query.addBehaviour(traceCost);
        return query;
    }

    public static <StateType extends State> ExplorationQuery<StateType> setupSweepLine(Function<StateType, Stream<Transition<StateType>>> neighbours, Comparator<StateType> progressMeasure) {
        final var frontier = TreeMapFrontier.withCollisions(progressMeasure, Objects::hashCode);
        final var heap = new HashSetHeap<StateType>();
        final var query = new ExplorationQuery<>(frontier, heap, neighbours);
        final var sweepLine = new SweepLineBehaviour<>(progressMeasure);
        query.addBehaviour(sweepLine);
        return query;
    }
}
