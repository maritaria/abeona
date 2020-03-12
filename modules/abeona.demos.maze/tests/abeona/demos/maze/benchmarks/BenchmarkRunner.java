package abeona.demos.maze.benchmarks;

import abeona.demos.maze.*;
import org.openjdk.jmh.annotations.*;

@Fork(value = 1, warmups = 0)
@Warmup(iterations = 0)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.SingleShotTime)
public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public void astar_abeona(DemoBestFirst demo) {
        //        System.out.println("Ready for astar");
        //        System.in.read();
        demo.benchmarkQuery.explore();
    }

    @Benchmark
    public void astar_handcrafted(BenchmarkHandcraftedAStar demo) {
        demo.exploreMazeHandcrafted();
    }

    @Benchmark
    public void bfs_abeona(DemoBfs demo) {
        demo.benchmarkQuery.explore();
    }

    @Benchmark
    public void bfs_handcrafted(BenchmarkHandcraftedBfs demo) {
        demo.exploreMazeHandcrafted();
    }

    @Benchmark
    public void dfs_abeona(DemoDfs demo) {
        demo.benchmarkQuery.explore();
    }

    @Benchmark
    public void dfs_handcrafted(BenchmarkHandcraftedDfs demo) {
        demo.exploreMazeHandcrafted();
    }

    //@Benchmark
    public void dijkstra(DemoDijkstra demo) {
        demo.benchmarkQuery.explore();
    }
}
