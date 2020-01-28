package abeona.demos.maze.gui;

import abeona.NextFunction;
import abeona.Query;
import abeona.TransitionEvaluationEvent;
import abeona.behaviours.AbstractBehaviour;
import abeona.behaviours.BacktraceBehaviour;
import abeona.behaviours.LogEventsBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.demos.maze.Maze;
import abeona.demos.maze.PlayerState;
import abeona.demos.maze.Position;
import abeona.frontiers.Frontier;
import abeona.frontiers.TreeMapFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;
import org.apache.commons.math3.analysis.function.Abs;

import java.util.Iterator;
import java.util.function.Consumer;

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
        final Frontier<PlayerState> frontier = TreeMapFrontier.withExactOrdering((playerState, t1) -> {
            double distance_a  = playerState.getLocation().getPos().distance(new Position(END_X, END_Y));
            double distance_b  = t1.getLocation().getPos().distance(new Position(END_X, END_Y));
            if (distance_a  <distance_b) {
                return -1;
            } else {
                return 1;
            }
        });

        // Pick the heap to use
        final Heap<PlayerState> heap = new HashSetHeap<>();

        // Pick the next-function
        final NextFunction<PlayerState> next = PlayerState::next;

        // Build the query
        final Query<PlayerState> query = new Query<>(frontier, heap, next);

        // You can add behaviours here
        query.addBehaviour(new LogEventsBehaviour<>());
        TerminateOnGoalStateBehaviour<PlayerState> terminalBehaviour = new TerminateOnGoalStateBehaviour<PlayerState>(x -> x.getLocation().getPos().equals(new Position(END_X, END_Y)));
        BacktraceBehaviour<PlayerState> backtrace = new BacktraceBehaviour<PlayerState>();
        query.addBehaviour(backtrace);

        terminalBehaviour.onGoal.tap(new Consumer<TransitionEvaluationEvent<PlayerState>>() {
            @Override
            public void accept(TransitionEvaluationEvent<PlayerState> playerStateTransitionEvaluationEvent) {
                Iterator<PlayerState> iterator = backtrace.iterateBackwardsTrace(playerStateTransitionEvaluationEvent.getTransition().getTargetState());
                for (Iterator<PlayerState> it = iterator; it.hasNext(); ) {
                    PlayerState stat = it.next();
                    System.out.println(stat);
                }
            }
        });
        query.addBehaviour(terminalBehaviour);

        return query;
    }
}
