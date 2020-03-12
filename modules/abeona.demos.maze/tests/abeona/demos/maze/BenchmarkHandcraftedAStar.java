package abeona.demos.maze;

import abeona.demos.maze.benchmarks.BenchmarkBase;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.*;

@State(Scope.Benchmark)
public class BenchmarkHandcraftedAStar extends BenchmarkBase {
    @Override
    public void prepareBenchmarkRun() {
        super.prepareBenchmarkRun();
        start = new Position(START_X, START_Y);
        exit = new Position(END_X, END_Y);
        frontier = new ArrayList<>(10);
        depths = new HashMap<>();
    }

    Position start = null;
    Position exit = null;
    ArrayList<Position> frontier = null;
    Map<Position, Integer> depths = null;

    @Test
    public void exploreMazeHandcrafted() {
        frontier.add(start);
        depths.put(start, 0);
        while (frontier.size() > 0) {
            final var pos = frontier.remove(0);
            if (pos.equals(exit)) {
                break;
            }
            frontier.ensureCapacity(4);
            final int depth = depths.get(pos);
            final var cell = maze.at(pos).orElseThrow();
            if (!cell.isWallLeft()) {
                tryCellMove(cell.getLeft().orElseThrow(), depth);
            }
            if (!cell.isWallRight()) {
                tryCellMove(cell.getRight().orElseThrow(), depth);
            }
            if (!cell.isWallTop()) {
                tryCellMove(cell.getTop().orElseThrow(), depth);
            }
            if (!cell.isWallBottom()) {
                tryCellMove(cell.getBottom().orElseThrow(), depth);
            }
        }
    }

    void tryCellMove(Maze.Cell cell, int currentDepth) {
        final var pos = cell.getPos();
        final boolean alreadyKnown = depths.containsKey(pos);
        if (alreadyKnown) {
            int knownDepth = depths.get(pos);
            if (currentDepth < knownDepth) {
                depths.put(pos, currentDepth);
                frontier.remove(pos);
                insertIntoFrontier(pos);
            }
        } else {
            depths.put(pos, currentDepth);
            insertIntoFrontier(pos);
        }
    }

    void insertIntoFrontier(Position item) {
        double distance = depths.get(item) + exit.distance(item);
        for (int i = 0; i < frontier.size(); i++) {
            final var other = frontier.get(i);
            final var otherDistance = (depths.get(other) + exit.distance(other));
            if (item.equals(other)) {
                return;
            }
            if (distance < otherDistance) {
                frontier.add(i, item);
                return;
            }
        }
        frontier.add(item);
    }
}
