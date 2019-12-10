package abeona.demos.maze;

import abeona.util.Arguments;
import ij.process.ImageProcessor;

import java.awt.*;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class MazeRenderer {
    private final Maze maze;
    private final ImageProcessor processor;

    public MazeRenderer(Maze maze) {
        Arguments.requireNonNull(maze, "maze");
        this.maze = maze;
        this.processor = MazeRendererCore.startImage(maze);
    }

    public MazeRenderer paintWalls(int startX, int endX) {
        MazeRendererCore.paintWalls(processor, maze, startX, endX);
        return this;
    }
    public MazeRenderer paintWalls() {
        MazeRendererCore.paintWalls(processor, maze);
        return this;
    }

    public MazeRenderer paintFloor(Function<Maze.Cell, Optional<Color>> floorColor) {
        MazeRendererCore.paintFloor(processor, maze, floorColor);
        return this;
    }

    public MazeRenderer paintHeap(Predicate<Maze.Cell> inHeap) {
        MazeRendererCore.paintHeap(processor, maze, inHeap);
        return this;
    }

    public MazeRenderer paintTrace(Iterator<Position> trace) {
        MazeRendererCore.paintTrace(processor, trace);
        return this;
    }

    public MazeRenderer paintLooseTrace(Iterator<Position> trace) {
        MazeRendererCore.paintLooseTrace(processor, trace);
        return this;
    }

    MazeRenderer save(String path) {
        MazeRendererCore.saveImage(processor, path);
        return this;
    }

    public Image toImage() {
        return processor.createImage();
    }
}
