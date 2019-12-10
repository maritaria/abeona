package abeona.demos.maze.gui;

import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.LogEventsBehaviour;
import abeona.demos.maze.Maze;
import abeona.demos.maze.MazeGenerator;
import abeona.demos.maze.PlayerState;
import abeona.frontiers.Frontier;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;

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
        final Frontier<PlayerState> frontier = QueueFrontier.fifoFrontier();

        // Pick the heap to use
        final Heap<PlayerState> heap = new HashSetHeap<>();

        // Pick the next-function
        final NextFunction<PlayerState> next = PlayerState::next;

        // Build the query
        final Query<PlayerState> query = new Query<>(frontier, heap, next);

        // You can add behaviours here
        query.addBehaviour(new LogEventsBehaviour<>());

        return query;
    }
}
