package abeona.demos.maze;

import abeona.demos.maze.benchmarks.BenchmarkBase;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@State(Scope.Benchmark)
public class BenchmarkHandcraftedDfs extends BenchmarkBase {
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
        try {
            explore(start);
        } catch (GoalFoundException ex) {
            // empty on purpose
        }
    }

    void explore(Position pos) {
        if (heap.contains(pos)) {
            return;
        }
        if (pos.equals(exit)) {
            throw new GoalFoundException();
        }
        heap.add(pos);
        final var cell = maze.at(pos).orElseThrow();
        if (!cell.isWallLeft()) {
            explore(cell.getLeft().orElseThrow().getPos());
        }
        if (!cell.isWallRight()) {
            explore(cell.getRight().orElseThrow().getPos());
        }
        if (!cell.isWallTop()) {
            explore(cell.getTop().orElseThrow().getPos());
        }
        if (!cell.isWallBottom()) {
            explore(cell.getBottom().orElseThrow().getPos());
        }
    }

    private static class GoalFoundException extends RuntimeException {}
}
