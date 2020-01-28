package abeona.demos.goatgame;

import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.LogEventsBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.frontiers.Frontier;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;

public class GoatProgram {
    public static void main(String[] args) {
        final var window = new GoatWindow();
        window.pack();
        window.setVisible(true);
    }

    static Query<GameState> createQuery() {
        // Pick the frontier to use
        final Frontier<GameState> frontier = QueueFrontier.fifoFrontier();

        // Pick the heap to use
        final Heap<GameState> heap = new HashSetHeap<>();

        // Pick the next-function
        final NextFunction<GameState> next = NextFunction.wrap(GameState::next);

        // Build the query
        final Query<GameState> query = new Query<>(frontier, heap, next);

        // You can add behaviours here
        query.addBehaviour(new LogEventsBehaviour<>());
        query.addBehaviour(new TerminateOnGoalStateBehaviour<>(state -> state.isGoal()));

        return query;
    }
}
