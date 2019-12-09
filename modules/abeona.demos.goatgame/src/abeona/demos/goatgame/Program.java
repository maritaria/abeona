package abeona.demos.goatgame;

import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.LogEventsBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;

public class Program {
    public static void main(String[] args) {
        final var window = new GoatWindow();
        window.pack();
        window.setVisible(true);
    }

    static Query<GameState> createQuery() {
        // Pick the frontier to use
        final var frontier = QueueFrontier.<GameState>fifoFrontier();

        // Pick the heap to use
        final var heap = new HashSetHeap<GameState>();

        // Pick the next-function
        final var next = NextFunction.wrap(GameState::next);

        // Build the query
        final var query = new Query<>(frontier, heap, next);

        // You can add behaviours here
        query.addBehaviour(new LogEventsBehaviour<>());

        return query;
    }
}
