package abeona.demos.pddl;

import abeona.NextFunction;
import abeona.Query;
import abeona.frontiers.Frontier;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;
import fr.uga.pddl4j.planners.statespace.StateSpacePlanner;
import fr.uga.pddl4j.util.BitState;
import fr.uga.pddl4j.util.Plan;

import java.util.stream.Stream;

import static abeona.demos.pddl.Helpers.*;

public final class PddlUsingAbeona {
    public static void main(String[] args) {
        final String domainFile = "pddl/gripper/domain.pddl";
        final String problemFile = "pddl/gripper/p01.pddl";
        final var problem = loadProblem(domainFile, problemFile);


        final Frontier<BitState> frontier = QueueFrontier.fifoFrontier();
        frontier.add(Stream.of(createInitialState(problem)));
        final Heap<BitState> heap = new HashSetHeap<>();
        final NextFunction<BitState> nextFunction = createNextFunction(problem);
        final Query<BitState> query = new Query<>(frontier, heap, nextFunction);

        final StateSpacePlanner planner = new AbeonaStateSpacePlanner(query);

        final Plan plan = planner.search(problem);
        if (plan != null) {
            System.out.println("Found plan as follows:");
            System.out.println(problem.toString(plan));
        } else {
            System.out.println("No plan found.");
        }
    }
}
