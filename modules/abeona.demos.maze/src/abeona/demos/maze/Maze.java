package abeona.demos.maze;

import abeona.util.Arguments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Maze {
    private final int width, height;
    private final HashMap<Position, Cell> cells;

    public Maze(int width, int height) {
        Arguments.requireMinimum(1, width, "width");
        Arguments.requireMinimum(1, height, "height");
        this.width = width;
        this.height = height;
        this.cells = new HashMap<>(width * height);
        // Fill cells
        final Consumer<Cell> add = c -> this.cells.put(c.getPos(), c);
        add.accept(new TopLeftCell());
        for (int x = 0; x < width; x++) {
            add.accept(new TopCell(x));
            for (int y = 0; y < height; y++) {
                add.accept(new Cell(new Position(x, y)));
            }
        }
        for (int y = 0; y < height; y++) {
            add.accept(new LeftCell(y));
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Optional<Cell> at(Position pos) {
        Arguments.requireNonNull(pos, "pos");
        final int x = pos.getX();
        final int y = pos.getY();
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Optional.empty();
        }
        return Optional.of(cells.get(pos));
    }

    Builder builder() {
        return new Builder();
    }

    final class Builder {
        Builder open() {
            Maze.this.cells.values().forEach(c -> {
                final var pos = c.getPos();
                c.setWallLeft(pos.getX() == 0);
                c.setWallTop(pos.getY() == 0);
            });
            return this;
        }

        Builder close() {
            Maze.this.cells.values().forEach(c -> {
                c.setWallLeft(true);
                c.setWallTop(true);
            });
            return this;
        }

        Builder openHorizontal(int startX, int startY, int length) {
            for (int x = startX; x < startX + length; x++) {
                final var pos = new Position(x, startY);
                Maze.this.at(pos).ifPresent(c -> c.setWallRight(true));
            }
            return this;
        }

        Builder openVertical(int startX, int startY, int length) {
            for (int y = startY; y < startY + length; y++) {
                final var pos = new Position(startX, y);
                Maze.this.at(pos).ifPresent(c -> c.setWallBottom(true));
            }
            return this;
        }
    }

    public class Cell {
        private final Position pos;
        private boolean wallLeft = false, wallTop = false;

        Cell(Position pos) {
            Arguments.requireNonNull(pos, "pos");
            this.pos = pos;
        }

        public Maze getMaze() {
            return Maze.this;
        }

        public Position getPos() {
            return pos;
        }

        public boolean isWallLeft() {
            return wallLeft;
        }

        public void setWallLeft(boolean wallLeft) {
            this.wallLeft = wallLeft;
        }

        public boolean isWallRight() {
            return getRight().map(Cell::isWallLeft).orElse(true);
        }

        public void setWallRight(boolean wallRight) {
            getRight().ifPresent(c -> c.setWallLeft(wallRight));
        }

        public boolean isWallTop() {
            return wallTop;
        }

        public void setWallTop(boolean wallTop) {
            this.wallTop = wallTop;
        }

        public boolean isWallBottom() {
            return getBottom().map(Cell::isWallTop).orElse(true);
        }

        public void setWallBottom(boolean wallDown) {
            getBottom().ifPresent(c -> c.setWallTop(wallDown));
        }

        public Optional<Cell> getLeft() {
            return Maze.this.at(pos.add(-1, 0));
        }

        public Optional<Cell> getRight() {
            return Maze.this.at(pos.add(1, 0));
        }

        public Optional<Cell> getTop() {
            return Maze.this.at(pos.add(0, -1));
        }

        public Optional<Cell> getBottom() {
            return Maze.this.at(pos.add(0, 1));
        }

        @Override
        public String toString() {
            return pos.toString() + "[left: " + wallLeft + ", top: " + wallTop + "]";
        }
    }

    class TopCell extends Cell {
        TopCell(int x) {
            super(new Position(x, 0));
        }

        @Override
        public boolean isWallTop() {
            return true;
        }

        @Override
        public void setWallTop(boolean wallTop) {
        }
    }

    class TopLeftCell extends TopCell {
        TopLeftCell() {
            super(0);
        }

        @Override
        public boolean isWallLeft() {
            return true;
        }

        @Override
        public void setWallLeft(boolean wallLeft) {
        }
    }

    class LeftCell extends Cell {
        LeftCell(int y) {
            super(new Position(0, y));
        }

        @Override
        public boolean isWallLeft() {
            return true;
        }

        @Override
        public void setWallLeft(boolean wallLeft) {
        }
    }
}
