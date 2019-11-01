package abeona.demos.maze;

import abeona.ExplorationQuery;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;

import java.util.stream.Stream;

class DemoBfs extends DemoBase {
    @Override
    String algorithmName() {
        return "bfs";
    }

    @Override
    ExplorationQuery<PlayerState> prepareQuery(Maze maze, Position start, Position end) {
        final var query = new ExplorationQuery<>(
                QueueFrontier.fifoFrontier(),
                new HashSetHeap<>(),
                PlayerState::next
        );
        query.getFrontier().add(Stream.of(new PlayerState(maze.at(start).orElseThrow())));
        return query;
    }
}
