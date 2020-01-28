import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.FrontierFilterBehaviour;
import abeona.behaviours.LogEventsBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.frontiers.Frontier;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;

import java.util.stream.Stream;

public class Program {
    public static void main(String[] args) {
        var query = createQuery();
        query.getFrontier().add(Stream.of(new RobotState(11, 0)));

        System.out.println(query.explore());
    }

    static Query<RobotState> createQuery() {
        // Pick the frontier to use
        final Frontier<RobotState> frontier = QueueFrontier.fifoFrontier();

        // Pick the heap to use
        final Heap<RobotState> heap = new HashSetHeap<>();

        // Pick the next-function
        final NextFunction<RobotState> next = NextFunction.wrap(s -> {
            RobotState s1 = s.climb();
            var s2 = s.climb();

            return Stream.of(s.climb(), s.descend()).filter(s3 -> !s3.isCrashed());
        });

        // Build the query
        final Query<RobotState> query = new Query<>(frontier, heap, next);

        // You can add behaviours here
        query.addBehaviour(new LogEventsBehaviour<>());
        query.addBehaviour(new TerminateOnGoalStateBehaviour<>(state -> state.isFinished()));
        query.addBehaviour(new FrontierFilterBehaviour<>(s -> s.getStepHeight() != 5));

        return query;
    }
}
