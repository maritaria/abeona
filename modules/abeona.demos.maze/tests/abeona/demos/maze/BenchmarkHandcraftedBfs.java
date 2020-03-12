package abeona.demos.maze;

import abeona.demos.maze.benchmarks.BenchmarkBase;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.*;

@State(Scope.Benchmark)
public class BenchmarkHandcraftedBfs extends BenchmarkBase {
    @Override
    public void prepareBenchmarkRun() {
        super.prepareBenchmarkRun();
        start = new Position(START_X, START_Y);
        exit = new Position(END_X, END_Y);
        frontier = new ArrayList<>(10);
        heap = new HashSet<>();
    }

    Position start = null;
    Position exit = null;
    ArrayList<Position> frontier = null;
    Set<Position> heap = null;

    @Test
    public void exploreMazeHandcrafted() {
        frontier.add(start);
        while (frontier.size() > 0) {
            final var pos = frontier.remove(0);
            if (pos.equals(exit)) {
                break;
            }
            frontier.ensureCapacity(4);
            final var cell = maze.at(pos).orElseThrow();
            if (!cell.isWallLeft()) {
                tryCellMove(cell.getLeft().orElseThrow());
            }
            if (!cell.isWallRight()) {
                tryCellMove(cell.getRight().orElseThrow());
            }
            if (!cell.isWallTop()) {
                tryCellMove(cell.getTop().orElseThrow());
            }
            if (!cell.isWallBottom()) {
                tryCellMove(cell.getBottom().orElseThrow());
            }
        }
    }

    void tryCellMove(Maze.Cell cell) {
        final var pos = cell.getPos();
        if (!heap.contains(pos)) {
            insertIntoFrontier(pos);
        }
    }

    void insertIntoFrontier(Position item) {
        frontier.add(item);
        heap.add(item);
    }
}
