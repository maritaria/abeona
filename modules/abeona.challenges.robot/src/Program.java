import abeona.NextFunction;
import abeona.Query;
import abeona.StateEvaluationEvent;
import abeona.TransitionEvaluationEvent;
import abeona.behaviours.BacktraceBehaviour;
import abeona.behaviours.FrontierFilterBehaviour;
import abeona.behaviours.LogEventsBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.frontiers.Frontier;
import abeona.frontiers.QueueFrontier;
import abeona.frontiers.TreeMapFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Program {
    public static void main(String[] args) {
        Query<RobotState> query = createQuery();
        query.getFrontier().add(Stream.of(new RobotState(0)));
        query.explore();
        System.out.println(counter);
    }

    private static int counter = 0;

    static Query<RobotState> createQuery() {
        // Pick the frontier to use
        final Frontier<RobotState> frontier = TreeMapFrontier.withExactOrdering((robotState, t1) -> robotState.height > t1.height ? -1 : 1);
        //final Frontier<RobotState> frontier = QueueFrontier.fifoFrontier();

        // Pick the heap to use
        final Heap<RobotState> heap = new HashSetHeap<>();

        // Pick the next-function
        final NextFunction<RobotState> next = RobotState::next;

        // Build the query
        final Query<RobotState> query = new Query<>(frontier, heap, next);

        // You can add behaviours here
        query.addBehaviour(new LogEventsBehaviour<>());
        query.addBehaviour(new TerminateOnGoalStateBehaviour<RobotState>(x -> x.height == RobotState.MAX_HEIGHT));
        List<Integer> broken = Arrays.asList()
        query.addBehaviour(new FrontierFilterBehaviour<RobotState>(x -> ));
        query.beforeStateEvaluation.tap(x -> counter++);
        return query;
    }
}
