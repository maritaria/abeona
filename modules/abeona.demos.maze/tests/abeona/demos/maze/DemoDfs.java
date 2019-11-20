package abeona.demos.maze;

import abeona.Query;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;

import java.util.stream.Stream;

public class DemoDfs extends DemoBase {
    @Override
    String algorithmName() {
        return "dfs";
    }

    @Override
    Query<PlayerState> prepareQuery(Maze maze, Position start, Position end) {
        final var query = new Query<>(
                QueueFrontier.lifoFrontier(),
                new HashSetHeap<>(),
                PlayerState::next
        );
        // TODO: Create behaviour that measures number of evaluations / states in the heap and such
        query.getFrontier().add(Stream.of(new PlayerState(maze.at(start).orElseThrow())));
        return query;
    }
}
