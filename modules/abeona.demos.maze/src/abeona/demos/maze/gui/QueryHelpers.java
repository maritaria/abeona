package abeona.demos.maze.gui;

import abeona.behaviours.TraceCostBehaviour;
import abeona.demos.maze.Maze;
import abeona.demos.maze.PlayerState;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;

import static abeona.demos.maze.gui.MazeProgram.END_X;
import static abeona.demos.maze.gui.MazeProgram.END_Y;

public class QueryHelpers {
    static Comparator<PlayerState> astar(Maze maze, TraceCostBehaviour<PlayerState> cost) {
        final var heuristic = distanceFunction(END_X, END_Y);
        return Comparator.<PlayerState>comparingDouble(state -> {
            var leading = cost.getTraceCost(state).orElse(0);
            var remaining = heuristic.applyAsDouble(state);
            return leading + remaining;
        })
                .thenComparingInt(state -> state.getLocation().getPos().getX())
                .thenComparingInt(state -> state.getLocation().getPos().getY());
    }

    static ToDoubleFunction<PlayerState> distanceFunction(int goalX, int goalY) {
        return (state) -> {
            final var pos = state.getLocation().getPos();
            final var dx = goalX - pos.getX();
            final var dy = goalY - pos.getY();
            return (dx * dx) + (dy * dy);
        };
    }

    static boolean isGoalState(PlayerState state) {
        final var location = state.getLocation();
        final var position = location.getPos();
        final var maze = location.getMaze();
        // Goal is the bottom right corner
        return position.getX() == END_X && position.getY() == END_Y;
    }
}
