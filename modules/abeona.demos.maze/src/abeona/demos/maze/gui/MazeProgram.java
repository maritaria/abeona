package abeona.demos.maze.gui;

import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.LogEventsBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.demos.maze.Maze;
import abeona.demos.maze.MazeGenerator;
import abeona.demos.maze.PlayerState;
import abeona.demos.maze.Position;
import abeona.frontiers.*;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;

import java.util.Comparator;
import java.util.Random;

public final class MazeProgram {
    // Maze size
    public static final int MAZE_WIDTH = 50;
    public static final int MAZE_HEIGHT = 50;
    // Starting position
    public static final int START_X = 0;
    public static final int START_Y = 0;
    // Goal cell: bottom right corner
    public static final int END_X = MAZE_WIDTH - 1;
    public static final int END_Y = MAZE_HEIGHT - 1;

    public static void main(String[] args) {
        final var simulator = new MazeSimulator();
        simulator.pack();
        simulator.setVisible(true);
    }

    static Query<PlayerState> createQuery(Maze maze) {
        // Pick the frontier to use
        final Frontier<PlayerState> frontier = TreeMapFrontier.withCollisions(Comparator.comparing(playerState -> {
            return playerState.getLocation().getPos().distance(new Position(END_X, END_Y));
        }), PlayerState::hashCode);

        // Pick the heap to use
        final Heap<PlayerState> heap = new HashSetHeap<>();

        // Pick the next-function
        final NextFunction<PlayerState> next = PlayerState::next;

        // Build the query
        final Query<PlayerState> query = new Query<>(frontier, heap, next);

        // You can add behaviours here
        query.addBehaviour(new LogEventsBehaviour<>());
        query.addBehaviour(new TerminateOnGoalStateBehaviour<>(playerState -> {
            return playerState.getLocation().getPos().getX() == END_X && playerState.getLocation().getPos().getY() == END_Y;
        }));

        return query;
    }
}
