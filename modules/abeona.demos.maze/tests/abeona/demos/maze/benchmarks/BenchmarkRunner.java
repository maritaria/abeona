package abeona.demos.maze.benchmarks;

import abeona.demos.maze.*;
import org.openjdk.jmh.annotations.*;

@Fork(value = 1, warmups = 0)
@Warmup(iterations = 0)
@Measurement(iterations = 3)
@BenchmarkMode(Mode.SingleShotTime)
public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public void handcrafted(DemoHandcrafted demo) {
        demo.exploreMazeHandcrafted();
    }
/*
    @Benchmark
    public void bfs(DemoBfs demo) {
        demo.benchmarkQuery.explore();
    }

    @Benchmark
    public void dfs(DemoDfs demo) {
        demo.benchmarkQuery.explore();
    }

    @Benchmark
    public void dijkstra(DemoDijkstra demo) {
        demo.benchmarkQuery.explore();
    }

    @Benchmark
    public void astar(DemoBestFirst demo) {
        demo.benchmarkQuery.explore();
    }
 //*/
}
