import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.LogEventsBehaviour;
import abeona.behaviours.SweepLineBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;

import javax.swing.plaf.basic.BasicBorders;
import java.util.stream.Stream;

public class Program {
    public static final int STAIR_HEIGHT = 1000;
    private static HashSetHeap<RobotState> heap = null;

    public static void main(String[] args) {
        final var query = createQuery();
        RobotState firstState = new RobotState(0, STAIR_HEIGHT);
        query.getFrontier().add(Stream.of(firstState));
        query.explore();
        System.out.println(heap.size());
    }

    static Query<RobotState> createQuery() {
        // Frontier
        final var frontier = QueueFrontier.<RobotState>fifoFrontier();

        // Heap
        final var heap = new HashSetHeap<RobotState>();
        Program.heap = heap;

        // Next function
        final NextFunction<RobotState> nextFunction = NextFunction.wrap(RobotState::next);

        // Setup query
        final var query = new Query<RobotState>(frontier, heap, nextFunction);

        // Add behaviours
        query.addBehaviour(new LogEventsBehaviour<>());
        query.addBehaviour(new TerminateOnGoalStateBehaviour<>(robotState -> {
            return robotState.getRobotState() == STAIR_HEIGHT;
        }));

        return query;
    }
}
