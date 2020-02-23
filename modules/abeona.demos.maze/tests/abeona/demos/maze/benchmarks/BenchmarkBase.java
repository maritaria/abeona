package abeona.demos.maze.benchmarks;

import abeona.demos.maze.Maze;
import abeona.demos.maze.MazeGenerator;
import org.openjdk.jmh.annotations.*;

import java.util.Random;

@State(Scope.Benchmark)
public class BenchmarkBase {
    public static int SEED = 1;
    public static int WIDTH = 200;
    public static int HEIGHT = 200;
    public static int START_X = WIDTH / 2;
    public static int START_Y = 0;
    public static int END_X = WIDTH - START_X;
    public static int END_Y = HEIGHT - 1;

    @Param({"8", "16", "32", "64", "128", "256", "512", "1024"})
    public int mazeSize;
    public Maze maze;

    @Setup(Level.Invocation)
    public void prepareBenchmarkRun() {
        setSize(mazeSize);
        this.maze = prepareMaze();
    }


    public static Maze prepareMaze() {
        return new MazeGenerator(new Random(SEED)).createMazeSubdiv(WIDTH, HEIGHT);
    }


    public static void setSize(int size) {
        WIDTH = size;
        HEIGHT = size;
        START_X = WIDTH / 2;
        START_Y = 0;
        END_X = WIDTH - START_X;
        END_Y = HEIGHT - 1;
    }
}
