package abeona.demos.maze;

import abeona.ExplorationQuery;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;

import java.util.stream.Stream;

class DemoDfs extends DemoBase {
    @Override
    String algorithmName() {
        return "dfs";
    }

    @Override
    ExplorationQuery<PlayerState> prepareQuery(Maze maze, Position start, Position end) {
        final var query = new ExplorationQuery<>(
                QueueFrontier.lifoFrontier(),
                new HashSetHeap<>(),
                PlayerState::next
        );
        // TODO: Create behaviour that measures number of evaluations / states in the heap and such
        query.getFrontier().add(Stream.of(new PlayerState(maze.at(start).orElseThrow())));
        return query;
    }
}