package abeona.demos.salesman;

import abeona.util.Arguments;

public final class Coordinate {
    private final int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double distance(Coordinate other) {
        Arguments.requireNonNull(other, "other");
        int dx = x - other.x;
        int dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public int hashCode() {
        return x ^ (y >>> 16) ^ (y << 16);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate) {
            final var other = (Coordinate) obj;
            return x == other.x && y == other.y;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
