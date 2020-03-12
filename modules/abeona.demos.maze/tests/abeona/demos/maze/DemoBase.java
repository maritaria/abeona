package abeona.demos.maze;

import abeona.Query;
import abeona.behaviours.BacktraceBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.demos.maze.benchmarks.BenchmarkBase;
import abeona.metadata.IsKnownOptimization;
import abeona.util.MappingIterator;
import org.junit.jupiter.api.Test;

public abstract class DemoBase extends BenchmarkBase {
    public static boolean OPTIMIZE = false;

    private Query<PlayerState> prepareQuery(Maze maze) {
        return prepareQuery(maze, new Position(START_X, START_Y), new Position(END_X, END_Y));
    }

    boolean isGoal(PlayerState state) {
        final var pos = state.getLocation().getPos();
        final int x = pos.getX();
        final int y = pos.getY();
        return x == END_X && y == END_Y;
    }

    abstract String algorithmName();

    abstract Query<PlayerState> prepareQuery(Maze maze, Position start, Position end);

    String renderExplorationOutputPath() {
        return "maze-demo." + algorithmName() + ".png";
    }

    @Test
    void renderExploration() {
        final var maze = prepareMaze();
        final var query = prepareQuery(maze);
        final var keepTrace = new BacktraceBehaviour<PlayerState>();
        keepTrace.attach(query);
        final var termination = new TerminateOnGoalStateBehaviour<>(this::isGoal);
        termination.attach(query);
        final var backtrace = termination.wrapExploration(query).map(keepTrace::iterateBackwardsTrace);
        final var renderer = new MazeRenderer(maze);
        renderer.paintHeap(c -> query.getHeap().contains(new PlayerState(c)));
        renderer.paintWalls(START_X, END_X);
        backtrace.ifPresent(playerStateIterator -> renderer
                .paintTrace(new MappingIterator<>(playerStateIterator, s -> s.getLocation().getPos())));
        renderer.save(renderExplorationOutputPath());
    }

    @Test
    void measureRuntimePerformance() {
        SEED = 1;
        final int warmup = 5;
        final int repeats = 10;
        final String name = algorithmName();
        System.out.println("Running warmup for " + name);
        for (int i = 0; i < warmup; i++) {
            SEED++;
            performanceRun();
        }
        System.out.println("Running main performance test for " + name);
        long totalRuntime = 0;
        for (int i = 0; i < repeats; i++) {
            SEED++;
            totalRuntime += performanceRun();
        }
        final long average = totalRuntime / repeats;
        System.out
                .println("Total runtime: " + totalRuntime + "ms, repeats: " + repeats + " average: " + average + "ms");
    }

    @Test
    long performanceRun() {
        final var maze = prepareMaze();
        final var query = prepareQuery(maze);
        final var termination = new TerminateOnGoalStateBehaviour<>(this::isGoal);
        termination.attach(query);
        if (OPTIMIZE) {
            new IsKnownOptimization<PlayerState>().attach(query);
        }
        final long start = System.currentTimeMillis();
        query.explore();
        final long end = System.currentTimeMillis();
        return end - start;
    }

    @Test
    void measureMemoryUsage() {
    }

    public Query<PlayerState> benchmarkQuery;

    @Override
    public void prepareBenchmarkRun() {
        super.prepareBenchmarkRun();
        benchmarkQuery = prepareQuery(maze);
        benchmarkQuery.addBehaviour(new TerminateOnGoalStateBehaviour<>(this::isGoal));
    }
}
