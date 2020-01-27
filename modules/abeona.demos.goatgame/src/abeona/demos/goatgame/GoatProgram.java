package abeona.demos.goatgame;

import abeona.ExplorationEvent;
import abeona.NextFunction;
import abeona.Query;
import abeona.StateEvaluationEvent;
import abeona.behaviours.LogEventsBehaviour;
import abeona.frontiers.Frontier;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;
import javafx.event.Event;

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
        final NextFunction<GameState> next = NextFunction.wrap(GameState::next);

        // Build the query
        final Query<GameState> query = new Query<>(frontier, heap, next);

        query.beforeStateEvaluation.tap(GoatProgram::beforeStateEvaluation);

        // You can add behaviours here
        query.addBehaviour(new LogEventsBehaviour<>());

        return query;
    }

    private static void beforeStateEvaluation(StateEvaluationEvent<GameState> event) {
        System.out.println("beforeExploration");

        GameState state = event.getSourceState();
        if (state.isGoal()){
            event.abortExploration();
        }
    }
}
