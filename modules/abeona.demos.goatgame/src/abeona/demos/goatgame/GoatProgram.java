package abeona.demos.goatgame;

import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.BacktraceBehaviour;
import abeona.behaviours.ExplorationBehaviour;
import abeona.behaviours.FrontierFilterBehaviour;
import abeona.behaviours.LogEventsBehaviour;
import abeona.frontiers.Frontier;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;

import java.util.stream.Stream;

public class GoatProgram {
    public static void main(String[] args) {
        final var window = new GoatWindow();
        window.pack();
        window.setVisible(true);
    }

    static Query<GameState> createQuery() {
        // Pick the frontier to use
        final Frontier<GameState> frontier = QueueFrontier.lifoFrontier();

        // Pick the heap to use
        final Heap<GameState> heap = new HashSetHeap<>();

        // Pick the next-function
        final NextFunction<GameState> next = NextFunction.wrap(gameState -> {
//            if (gameState.isGoal()) {
//                frontier.clear();
//                return Stream.empty();
//            } else {
                return gameState.next();
            //}
        });

        // Build the query
        final Query<GameState> query = new Query<>(frontier, heap, next);

        // You can add behaviours here
        query.addBehaviour(new LogEventsBehaviour<>());
        query.addBehaviour(new ExplorationBehaviour<GameState>() {
            @Override
            public void attach(Query<GameState> query) {
                query.afterStateEvaluation.tap(event -> {
                    if (event.getSourceState().isGoal()) frontier.clear();
                });
            }

            @Override
            public void detach(Query<GameState> query) {

            }
        });

        return query;
    }
}
