import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.LogEventsBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.frontiers.Frontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;

import java.util.Set;

public class Program {
    public static void main(String[] args) {

        int maxLevel = 1009;
        Set<Integer> stepSizes = Set.of(7, -3, 5);
        RobotState initial = new RobotState(maxLevel, stepSizes);

        Frontier<RobotState> frontier = new RobotFrontier(initial);

        Heap<RobotState> heap = new HashSetHeap<>();

        NextFunction<RobotState> next = NextFunction.wrap(RobotState::next);

        Query<RobotState> query = new Query<>(frontier, heap, next);

        query.addBehaviour(new LogEventsBehaviour<>());
        query.addBehaviour(new TerminateOnGoalStateBehaviour<>(RobotState::isGoal));

        query.explore();
    }

}
