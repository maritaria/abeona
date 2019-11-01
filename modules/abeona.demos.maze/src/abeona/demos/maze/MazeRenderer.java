package abeona.demos.maze;

import abeona.util.Arguments;
import ij.process.ImageProcessor;

import java.util.Iterator;
import java.util.function.Predicate;

public class MazeRenderer {
    private final Maze maze;
    private final ImageProcessor processor;

    MazeRenderer(Maze maze) {
        Arguments.requireNonNull(maze, "maze");
        this.maze = maze;
        this.processor = MazeRendererCore.startImage(maze);
    }

    MazeRenderer paintWalls(int startX, int endX) {
        MazeRendererCore.paintWalls(processor, maze, startX, endX);
        return this;
    }

    MazeRenderer paintHeap(Predicate<Maze.Cell> inHeap) {
        MazeRendererCore.paintHeap(processor, maze, inHeap);
        return this;
    }

    MazeRenderer paintTrace(Iterator<Position> trace) {
        MazeRendererCore.paintTrace(processor, trace);
        return this;
    }

    MazeRenderer paintLooseTrace(Iterator<Position> trace) {
        MazeRendererCore.paintLooseTrace(processor, trace);
        return this;
    }

    MazeRenderer save(String path) {
        MazeRendererCore.saveImage(processor, path);
        return this;
    }
}
