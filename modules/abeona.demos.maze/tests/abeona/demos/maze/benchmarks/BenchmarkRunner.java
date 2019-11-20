package abeona.demos.maze.benchmarks;

import abeona.demos.maze.DemoBestFirst;
import abeona.demos.maze.DemoBfs;
import abeona.demos.maze.DemoDfs;
import abeona.demos.maze.DemoDijkstra;
import org.openjdk.jmh.annotations.*;

public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 5)
    @BenchmarkMode(Mode.AverageTime)
    public void bfs(DemoBfs demo) {
        demo.benchmarkQuery.explore();
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 5)
    @BenchmarkMode(Mode.AverageTime)
    public void dfs(DemoDfs demo) {
        demo.benchmarkQuery.explore();
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 5)
    @BenchmarkMode(Mode.AverageTime)
    public void dijkstra(DemoDijkstra demo) {
        demo.benchmarkQuery.explore();
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 5)
    @BenchmarkMode(Mode.AverageTime)
    public void astar(DemoBestFirst demo) {
        demo.benchmarkQuery.explore();
    }
}
