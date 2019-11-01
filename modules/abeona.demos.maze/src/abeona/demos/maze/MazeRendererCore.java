package abeona.demos.maze;

import abeona.util.Arguments;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

public class MazeRendererCore {
    private static final int cellSize = 12;
    private static final int cellCenter = cellSize / 2;
    // Colors
    private static final int colorBits = 8;

    private static int rgb(int red, int green, int blue) {
        int colorMax = (1 << colorBits) - 1;
        int redBits = red & colorMax;
        int greenBits = green & colorMax;
        int blueBits = blue & colorMax;
        return redBits << (colorBits * 2) | greenBits << colorBits | blueBits;
    }

    private static final int colorFloor = rgb(255, 255, 255);
    private static final int colorWall = rgb(0, 0, 0);
    private static final int colorHeap = rgb(51, 153, 255);
    private static final int colorTrace = rgb(255, 0, 0);

    static int cellStart(int x) {
        return x * cellSize;
    }

    static int cellEnd(int x) {
        return ((x + 1) * cellSize);
    }

    static int cellCenter(int x) {
        return (x * cellSize) + cellCenter;
    }

    static ImageProcessor startImage(Maze maze) {
        final var width = maze.getWidth();
        final var height = maze.getHeight();
        final var imageWidth = (width * cellSize) + 1;
        final var imageHeight = (height * cellSize) + 1;
        final var process = new ColorProcessor(imageWidth, imageHeight);
        process.setColor(colorFloor);
        process.fill();
        return process;
    }

    static void saveImage(ImageProcessor processor, String path) {
        var image = new ImagePlus("", processor);
        var saver = new FileSaver(image);
        saver.saveAsPng(path);
    }

    static void paintTrace(ImageProcessor processor, Iterator<Position> positions) {
        ArrayList<Position> trace = new ArrayList<>(20);
        if (!positions.hasNext()) {
            paintLooseTrace(processor, positions);
            return;
        }
        final var first = positions.next();
        trace.add(new Position(first.getX(), first.getY() + 1));
        trace.add(first);
        while (positions.hasNext()) {
            trace.add(positions.next());
        }
        final var last = trace.get(trace.size() - 1);
        trace.add(new Position(last.getX(), last.getY() - 1));
        paintLooseTrace(processor, trace.iterator());
    }

    static void paintLooseTrace(ImageProcessor processor, Iterator<Position> positions) {
        if (!positions.hasNext()) return;
        processor.setColor(colorTrace);
        // First line entrance
        var previousPosition = positions.next();
        if (positions.hasNext()) {
            processor.moveTo(
                    cellCenter(previousPosition.getX()),
                    cellCenter(previousPosition.getY())
            );
            do {
                final var position = positions.next();
                processor.lineTo(
                        cellCenter(position.getX()),
                        cellCenter(position.getY())
                );
            } while (positions.hasNext());
        } else {
            processor.drawDot(
                    cellCenter(previousPosition.getX()),
                    cellCenter(previousPosition.getY())
            );
        }
    }

    static void paintHeap(ImageProcessor processor, Maze maze, Predicate<Maze.Cell> inHeap) {
        processor.setColor(colorHeap);
        final var width = maze.getWidth();
        final var height = maze.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final var pos = new Position(x, y);
                final var cell = maze.at(pos).orElseThrow();
                if (inHeap.test(cell)) {
                    processor.fillPolygon(new Polygon(new int[]{
                            cellStart(pos.getX()),
                            cellEnd(pos.getX()) + 1,
                            cellEnd(pos.getX()) + 1,
                            cellStart(pos.getX()),
                    }, new int[]{
                            cellStart(pos.getY()),
                            cellStart(pos.getY()),
                            cellEnd(pos.getY()) + 1,
                            cellEnd(pos.getY()) + 1,
                    }, 4));
                }
            }
        }
    }

    static void paintWalls(ImageProcessor processor, Maze maze, int startX, int exitX) {
        processor.setColor(colorWall);
        final var width = maze.getWidth();
        final var height = maze.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final var cell = maze.at(new Position(x, y)).orElseThrow();
                if (cell.isWallTop()) {
                    if (y != 0 || x != startX) {
                        processor.drawLine(
                                cellStart(x),
                                cellStart(y),
                                cellEnd(x),
                                cellStart(y)
                        );
                    }
                }
                if (cell.isWallLeft()) {
                    processor.drawLine(
                            cellStart(x),
                            cellStart(y),
                            cellStart(x),
                            cellEnd(y)
                    );
                }
                if (y == height - 1 && cell.isWallBottom()) {
                    if (x != exitX) {
                        processor.drawLine(
                                cellStart(x),
                                cellEnd(y),
                                cellEnd(x),
                                cellEnd(y)
                        );
                    }
                }
                if (x == width - 1 && cell.isWallRight()) {
                    processor.drawLine(
                            cellEnd(x),
                            cellStart(y),
                            cellEnd(x),
                            cellEnd(y)
                    );
                }
            }
        }
    }
}
