package abeona.demos.sokoban;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class SokobanLevel {
    private final int width;
    private final int height;
    private final boolean[][] walls;
    private final Set<Position> buttons = new HashSet<>();

    public SokobanLevel(int width, int height) {
        this.width = width;
        this.height = height;
        this.walls = new boolean[width][height];
    }

    public int getWidth() {return width;}

    public int getHeight() {return height;}

    public Stream<Position> getButtons() {
        return buttons.stream();
    }

    public boolean isButton(Position pos) {
        return getButtons().anyMatch(p -> p.equals(pos));
    }

    public void addButton(Position pos) {
        buttons.add(pos);
    }

    public boolean isWall(Position pos) {
        return isWall(pos.getX(), pos.getY());
    }

    public boolean isWall(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return true;
        }
        return walls[x][y];
    }

    public void setWall(int x, int y, boolean state) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            walls[x][y] = state;
        }
    }
}
