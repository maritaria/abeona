package abeona.demos.maze;

import abeona.ExplorationQuery;
import abeona.behaviours.TraceCostFrontierBehaviour;
import abeona.frontiers.TreeMapFrontier;
import abeona.heaps.HashSetHeap;

import java.util.Comparator;
import java.util.stream.Stream;

class DemoDijkstra extends DemoBase {
    @Override
    String algorithmName() {
        return "dijkstra";
    }

    @Override
    ExplorationQuery<PlayerState> prepareQuery(Maze maze, Position start, Position end) {
        final var cost = new TraceCostFrontierBehaviour<PlayerState>(t -> 1);
        final var comp = Comparator
                .<PlayerState>comparingDouble(state -> cost.getTraceCost(state).orElse(0))
                .thenComparingInt(state -> state.getLocation().getPos().getX())
                .thenComparingInt(state -> state.getLocation().getPos().getY());
        final var frontier = TreeMapFrontier.withExactOrdering(comp);
        final var heap = new HashSetHeap<PlayerState>();
        final var query = new ExplorationQuery<>(frontier, heap, PlayerState::next);
        cost.attach(query);
        query.getFrontier().add(Stream.of(new PlayerState(maze.at(start).orElseThrow())));
        return query;
    }
}
