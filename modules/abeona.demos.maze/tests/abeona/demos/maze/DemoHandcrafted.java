package abeona.demos.maze;

import abeona.demos.maze.benchmarks.BenchmarkBase;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@State(Scope.Benchmark)
public class DemoHandcrafted extends BenchmarkBase {
    @Test
    public void exploreMazeHandcrafted() {
        final Position start = new Position(START_X, START_Y);
        final Position exit = new Position(END_X, END_Y);
        final List<Position> frontier = new ArrayList<>();
        final Set<Position> heap = new HashSet<>();
        frontier.add(start);
        while (frontier.size() > 0) {
            final var pos = frontier.remove(0);
            if (pos.equals(exit)) {
                break;
            }
            final var cell = maze.at(pos).orElseThrow();
            heap.add(pos);
            if (!cell.isWallLeft()) {
                tryCellMove(frontier, heap, cell.getLeft().orElseThrow(), exit);
            }
            if (!cell.isWallRight()) {
                tryCellMove(frontier, heap, cell.getRight().orElseThrow(), exit);
            }
            if (!cell.isWallTop()) {
                tryCellMove(frontier, heap, cell.getTop().orElseThrow(), exit);
            }
            if (!cell.isWallBottom()) {
                tryCellMove(frontier, heap, cell.getBottom().orElseThrow(), exit);
            }
        }
    }

    void tryCellMove(List<Position> frontier, Set<Position> heap, Maze.Cell cell, Position exit) {
        final var pos = cell.getPos();
        if (!heap.contains(pos)) {
            insertIntoFrontier(frontier, pos, exit);
        }
    }

    void insertIntoFrontier(List<Position> frontier, Position item, Position exit) {
        double distance = exit.distance(item);
        for (int i = 0; i < frontier.size(); i++) {
            final var other = frontier.get(i);
            double otherDistance = exit.distance(other);
            if (distance < otherDistance) {
                frontier.add(i, item);
                return;
            }
        }
        frontier.add(item);
    }
}
