package benchmarks;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.planners.ProblemFactory;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
public abstract class BenchmarkItem {
    @Param({
            "pddl/blocksworld/p01.pddl",
            "pddl/blocksworld/p02.pddl",
            "pddl/blocksworld/p03.pddl",
            "pddl/blocksworld/p04.pddl",
            "pddl/blocksworld/p05.pddl",
            "pddl/blocksworld/p06.pddl",
            "pddl/blocksworld/p07.pddl",
            "pddl/blocksworld/p08.pddl",

            "pddl/depots/p01.pddl",
            "pddl/depots/p02.pddl",

            "pddl/gripper/p01.pddl",
            "pddl/gripper/p02.pddl",
            "pddl/gripper/p03.pddl",
            "pddl/gripper/p04.pddl",
    })
    public String problemPath;

    public CodedProblem problem;

    private CodedProblem loadProblem() {
        final var problemFile = new File(problemPath);
        final var problemFolder = problemFile.getParent();
        final var domainFile = new File(problemFolder + File.separator + "domain.pddl");

        // Load up the factory
        final ProblemFactory factory = ProblemFactory.getInstance();
        ErrorManager errorManager = null;
        try {
            errorManager = factory.parse(domainFile, problemFile);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error when parsing the PDDL planning problem description.", e);
        }
        // Report problems
        if (!errorManager.isEmpty()) {
            throw new RuntimeException("Files contained problems: " + System.lineSeparator() + errorManager
                    .getMessages().stream().map(Message::toString).collect(Collectors.joining(System.lineSeparator())));
        }
        // Build the encoded problem
        final CodedProblem encoded = factory.encode();
        if (!encoded.isSolvable()) {
            throw new RuntimeException("Goal can be simplified to FALSE. No search will solve it.");
        }
        return encoded;
    }

    @Setup(Level.Invocation)
    public void setupBenchmark() {
        problem = loadProblem();
    }

    public abstract void run();
}
