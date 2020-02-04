package abeona.demos.pddl;

import abeona.NextFunction;
import abeona.Query;
import abeona.Transition;
import abeona.behaviours.BacktraceBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.frontiers.Frontier;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;
import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.util.BitOp;
import fr.uga.pddl4j.util.BitState;
import fr.uga.pddl4j.util.CondBitExp;

import java.util.stream.Stream;

import static abeona.demos.pddl.Helpers.*;

public final class AbeonaUsingPddl {
    public static void main(String[] args) {
        final String domainFile = "pddl/gripper/domain.pddl";
        final String problemFile = "pddl/gripper/p01.pddl";
        final var problem = loadProblem(domainFile, problemFile);

        final var query = createAbeonaQuery(problem);

        final var backtraceBehaviour = new BacktraceBehaviour<BitState>();
        query.addBehaviour(backtraceBehaviour);

        final var goalBehaviour = new TerminateOnGoalStateBehaviour<>(createGoalPredicate(problem));
        query.addBehaviour(goalBehaviour);

        goalBehaviour.onGoal.tap(event -> {
            final var state = event.getTransition().getTargetState();
            System.out.println("Goal state found:\n" + problem.toString(state) + "\nTrace:");
            final var backtrace = backtraceBehaviour.iterateBackwardsTrace(state);
            while (backtrace.hasNext()) {
                final var next = backtrace.next();
                System.out.println(problem.toString(next) + "\n");
            }
            System.out.println("(initial state)");
        });

        query.explore();
    }

    private static Query<BitState> createAbeonaQuery(CodedProblem problem) {
        final Frontier<BitState> frontier = QueueFrontier.fifoFrontier();
        frontier.add(Stream.of(createInitialState(problem)));
        final Heap<BitState> heap = new HashSetHeap<>();
        final NextFunction<BitState> nextFunction = createNextFunction(problem);
        return new Query<>(frontier, heap, nextFunction);
    }

}
