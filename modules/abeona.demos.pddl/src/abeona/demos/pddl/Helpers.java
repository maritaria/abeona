package abeona.demos.pddl;

import abeona.NextFunction;
import abeona.Transition;
import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.planners.ProblemFactory;
import fr.uga.pddl4j.util.BitOp;
import fr.uga.pddl4j.util.BitState;
import fr.uga.pddl4j.util.CondBitExp;

import java.io.IOException;
import java.util.function.Predicate;

public final class Helpers {

    static CodedProblem loadProblem(String domainFile, String problemFile) {
        // Load up the factory
        final ProblemFactory factory = ProblemFactory.getInstance();
        ErrorManager errorManager = null;
        try {
            errorManager = factory.parse(domainFile, problemFile);
        } catch (IOException e) {
            System.out.println("Unexpected error when parsing the PDDL planning problem description.");
            System.exit(0);
        }
        // Report problems
        if (!errorManager.isEmpty()) {
            errorManager.printAll();
            System.exit(0);
        } else {
            System.out.println("Parsing domain file and problem file done successfully");
        }
        // Build the encoded problem
        final CodedProblem encoded = factory.encode();
        System.out.println("Encoding problem done successfully (" + encoded.getOperators()
                .size() + " ops, " + encoded.getRelevantFacts().size() + " facts).");
        if (!encoded.isSolvable()) {
            System.out.println("Goal can be simplified to FALSE. No search will solve it.");
            System.exit(0);
        }
        return encoded;
    }

    static BitState createInitialState(CodedProblem problem) {
        return new BitState(problem.getInit());
    }


    static NextFunction<BitState> createNextFunction(CodedProblem problem) {
        return state -> problem.getOperators()
                .stream()
                .filter(op -> op.isApplicable(state))
                .map(op -> applyEffects(state, op));
    }

    private static Transition<BitState> applyEffects(BitState state, BitOp op) {
        var next = new BitState(state);
        op.getCondEffects()
                .stream()
                .filter(effect -> next.satisfy(effect.getCondition()))
                .map(CondBitExp::getEffects)
                .forEach(next::apply);
        return new Transition<>(state, next, op);
    }

    static Predicate<BitState> createGoalPredicate(CodedProblem problem) {
        return state -> state.satisfy(problem.getGoal());
    }
}
