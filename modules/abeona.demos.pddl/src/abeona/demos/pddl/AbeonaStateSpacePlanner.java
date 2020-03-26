package abeona.demos.pddl;

import abeona.Query;
import abeona.Transition;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.planners.statespace.AbstractStateSpacePlanner;
import fr.uga.pddl4j.util.BitOp;
import fr.uga.pddl4j.util.BitState;
import fr.uga.pddl4j.util.Plan;
import fr.uga.pddl4j.util.SequentialPlan;

import java.util.LinkedList;
import java.util.stream.Stream;

import static abeona.demos.pddl.Helpers.createGoalPredicate;

public class AbeonaStateSpacePlanner extends AbstractStateSpacePlanner {
    private final Query<BitState> query;

    public AbeonaStateSpacePlanner(Query<BitState> query) {
        if (query == null) {
            throw new IllegalArgumentException("query is null");
        }
        this.query = query;
    }

    @Override
    public Plan search(CodedProblem codedProblem) {
        // Wipe the frontier of any leftovers
        query.getFrontier().clear();
        // Setup the initial state of the problem
        query.getFrontier().add(Stream.of(new BitState(codedProblem.getInit())));
        // Keep track of a backtrace, but the default of abeona does not store transitions
        // The plan requires the BitOp assigned to transitions, so this behaviour stores the transitions instead
        final var backtraceBehaviour = new PddlBacktraceBehaviour();
        query.addBehaviour(backtraceBehaviour);
        // Setup the termination behaviour so we can easily wrap with logic later
        final var goalBehaviour = new TerminateOnGoalStateBehaviour<>(createGoalPredicate(codedProblem));
        query.addBehaviour(goalBehaviour);
        // Perform the query in a try-finally so we can be sure the detach logic of the behaviour runs
        try {
            // Execute the query with the wrapper utility
            return goalBehaviour.wrapExploration(query).map(state -> {
                // We now know the goal state, with backtraces we can build the plan
                final var backtrace = backtraceBehaviour.iterateBackwardsTrace(state);
                final var trace = new LinkedList<Transition<BitState>>();
                // Collect the trace into a linked list so we can traverse it in forward order
                while (backtrace.hasNext()) {
                    final var next = backtrace.next();
                    trace.addFirst(next);
                }
                // Build the plan
                final var plan = new SequentialPlan();
                for (var step : trace) {
                    final var operation = (BitOp) step.getUserdata();
                    plan.add(plan.size(), operation);
                }
                return plan;
            }).orElse(null);
        } finally {
            // Once done we remove the behaviours so we don't leave them lingering around
            query.removeBehaviour(backtraceBehaviour);
            query.removeBehaviour(goalBehaviour);
        }
    }
}
