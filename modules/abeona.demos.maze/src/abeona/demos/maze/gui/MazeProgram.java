package abeona.demos.maze.gui;

import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.LogEventsBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.demos.maze.Maze;
import abeona.demos.maze.PlayerState;
import abeona.demos.maze.Position;
import abeona.frontiers.Frontier;
import abeona.frontiers.TreeMapFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;

import java.util.Comparator;

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

        final Frontier<PlayerState> frontier = TreeMapFrontier.withCollisions(Comparator.comparingDouble(playerState1 -> {
            return playerState1.getLocation().getPos().distance(new Position(END_X, END_Y));
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
            final var pos = playerState.getLocation().getPos();
            return pos.getX() == END_X && pos.getY() == END_Y;
        }));

        return query;
    }

}
