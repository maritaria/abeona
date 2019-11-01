package abeona.demos.maze;

import abeona.util.Arguments;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MazeGenerator {
    private final Random random;

    public MazeGenerator(Random random) {
        Arguments.requireNonNull(random, "random");
        this.random = random;
    }

    <T> T popRandom(List<T> source) {
        return source.remove(random.nextInt(source.size()));
    }

    Maze createMazeDfs(int width, int height) {
        final var result = new Maze(width, height);
        result.builder().close();
        final Set<Maze.Cell> visited = new HashSet<>(width * height);
        final Deque<Maze.Cell> frontier = new ArrayDeque<>(width + height);
        frontier.add(result.at(new Position(width / 2, width / 2)).orElseThrow());
        while (!frontier.isEmpty()) {
            final var current = frontier.pop();
            final var available = new ArrayList<AvailablePair>(4);
            final Predicate<? super Maze.Cell> isUnknown = Predicate.not(visited::contains).and(Predicate.not(frontier::contains));
            if (current.isWallLeft()) {
                current.getLeft()
                        .filter(isUnknown)
                        .map(c -> new AvailablePair(c, Maze.Cell::setWallLeft))
                        .ifPresent(available::add);
            }
            if (current.isWallRight()) {
                current.getRight()
                        .filter(isUnknown)
                        .map(c -> new AvailablePair(c, Maze.Cell::setWallRight))
                        .ifPresent(available::add);
            }
            if (current.isWallTop()) {
                current.getTop()
                        .filter(isUnknown)
                        .map(c -> new AvailablePair(c, Maze.Cell::setWallTop))
                        .ifPresent(available::add);
            }
            if (current.isWallBottom()) {
                current.getBottom()
                        .filter(isUnknown)
                        .map(c -> new AvailablePair(c, Maze.Cell::setWallBottom))
                        .ifPresent(available::add);
            }
            if (!available.isEmpty()) {
                available.sort(Comparator.comparingInt(p -> random.nextInt()));
                final var next = popRandom(available);
                next.waller.accept(current, false);
                visited.add(next.target);
                if (frontier.isEmpty()) {
                    for (int i = 0; i < 10; i++) {
                        visited.stream().skip(random.nextInt(visited.size())).findFirst().ifPresent(frontier::add);
                    }
                }
                if (!frontier.isEmpty() && random.nextInt(10) > 1) {
                    frontier.addFirst(current);
                } else {
                    frontier.add(current);
                }
                frontier.addFirst(next.target);
            }
        }
        return result;
    }

    Maze createMazePrim(int width, int height) {
        final var result = new Maze(width, height);
        result.builder().close();
        final Set<Maze.Cell> visited = new HashSet<>(width * height);
        final List<Maze.Cell> frontier = new ArrayList<Maze.Cell>(width + height);
        frontier.add(result.at(new Position(0, 0)).orElseThrow());
        while (!frontier.isEmpty()) {
            final var current = popRandom(frontier);
            visited.add(current);
            final var available = new ArrayList<AvailablePair>(4);
            if (current.isWallLeft()) {
                current.getLeft()
                        .filter(Predicate.not(visited::contains))
                        .map(c -> new AvailablePair(c, Maze.Cell::setWallLeft))
                        .ifPresent(available::add);
            }
            if (current.isWallRight()) {
                current.getRight()
                        .filter(Predicate.not(visited::contains))
                        .map(c -> new AvailablePair(c, Maze.Cell::setWallRight))
                        .ifPresent(available::add);
            }
            if (current.isWallTop()) {
                current.getTop()
                        .filter(Predicate.not(visited::contains))
                        .map(c -> new AvailablePair(c, Maze.Cell::setWallTop))
                        .ifPresent(available::add);
            }
            if (current.isWallBottom()) {
                current.getBottom()
                        .filter(Predicate.not(visited::contains))
                        .map(c -> new AvailablePair(c, Maze.Cell::setWallBottom))
                        .ifPresent(available::add);
            }

            if (!available.isEmpty()) {
                final var next = popRandom(available);
                frontier.add(next.target);
                visited.add(next.target);
                next.waller.accept(current, false);
                if (!available.isEmpty()) {
                    frontier.add(current);
                }
            }
        }
        return result;
    }

    private class AvailablePair {
        Maze.Cell target;
        BiConsumer<Maze.Cell, Boolean> waller;

        AvailablePair(Maze.Cell target, BiConsumer<Maze.Cell, Boolean> waller) {
            this.target = target;
            this.waller = waller;
        }
    }

    Maze createMazeSubdiv(int width, int height) {
        final var maze = new Maze(width, height);
        maze.builder().open();
        subdivide(maze, 0, 0, width, height,
                pos -> maze.at(pos).orElseThrow().setWallLeft(true),
                pos -> maze.at(new Position(pos.getY(), pos.getX())).orElseThrow().setWallTop(true)
        );
        return maze;
    }

    private void subdivide(Maze maze, int startX, int startY, int width, int height, Consumer<Position> applyX, Consumer<Position> applyY) {
        if (width < height) {
            subdivide(maze, startY, startX, height, width, applyY, applyX);
        } else {
            if (width <= 1 || height <= 1) return;
            int halfwayX = 1 + random.nextInt(width - 1);
            int openingY = startY + random.nextInt(height);
            for (int y = startY; y < (startY + height); y++) {
                if (y != openingY) {
                    applyX.accept(new Position(startX + halfwayX, y));
                }
            }
            subdivide(maze, startX, startY, halfwayX, height, applyX, applyY);
            subdivide(maze, startX + halfwayX, startY, width - halfwayX, height, applyX, applyY);

        }
    }
}
