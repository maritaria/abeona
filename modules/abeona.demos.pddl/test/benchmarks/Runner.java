package benchmarks;

import abeona.NextFunction;
import abeona.Query;
import abeona.demos.pddl.AbeonaStateSpacePlanner;
import abeona.frontiers.Frontier;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;
import fr.uga.pddl4j.planners.Planner;
import fr.uga.pddl4j.planners.statespace.generic.GenericPlanner;
import fr.uga.pddl4j.planners.statespace.search.strategy.BreadthFirstSearch;
import fr.uga.pddl4j.planners.statespace.search.strategy.DepthFirstSearch;
import fr.uga.pddl4j.util.BitState;
import org.openjdk.jmh.annotations.*;

import java.util.stream.Stream;

import static abeona.demos.pddl.Helpers.createInitialState;
import static abeona.demos.pddl.Helpers.createNextFunction;

@Fork(value = 1, warmups = 0)
@Warmup(iterations = 0)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.SingleShotTime)
public class Runner {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public void abeona_bfs(AbeonaBfsPlanner item) {
        item.run();
    }

    @Benchmark
    public void abeona_dfs(AbeonaDfsPlanner item) {
        item.run();
    }

    @Benchmark
    public void pddl4j_bfs(Pddl4jBfsPlanner item) {
        item.run();
    }

    @Benchmark
    public void pddl4j_dfs(Pddl4jDfsPlanner item) {
        item.run();
    }

    public static class AbeonaBfsPlanner extends Pddl4JPlannerBenchmark {
        @Override
        protected Planner setupPlanner() {
            final Frontier<BitState> frontier = QueueFrontier.fifoFrontier();
            frontier.add(Stream.of(createInitialState(problem)));
            final Heap<BitState> heap = new HashSetHeap<>();
            final NextFunction<BitState> nextFunction = createNextFunction(problem);
            final Query<BitState> query = new Query<>(frontier, heap, nextFunction);
            return new AbeonaStateSpacePlanner(query);
        }
    }

    public static class AbeonaDfsPlanner extends Pddl4JPlannerBenchmark {
        @Override
        protected Planner setupPlanner() {
            final Frontier<BitState> frontier = QueueFrontier.lifoFrontier();
            frontier.add(Stream.of(createInitialState(problem)));
            final Heap<BitState> heap = new HashSetHeap<>();
            final NextFunction<BitState> nextFunction = createNextFunction(problem);
            final Query<BitState> query = new Query<>(frontier, heap, nextFunction);
            return new AbeonaStateSpacePlanner(query);
        }
    }

    public static class Pddl4jBfsPlanner extends Pddl4JPlannerBenchmark {
        @Override
        protected Planner setupPlanner() {
            return new GenericPlanner(new BreadthFirstSearch());
        }
    }

    public static class Pddl4jDfsPlanner extends Pddl4JPlannerBenchmark {
        @Override
        protected Planner setupPlanner() {
            return new GenericPlanner(new DepthFirstSearch());
        }
    }
}
