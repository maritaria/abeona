package abeona.demos.sokoban;

import abeona.util.Arguments;

public class Position {
    private final int x;
    private final int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return this.x ^ (this.y >> 16);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            final var other = (Position) obj;
            return x == other.x && y == other.y;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public Position add(int x, int y) {
        return new Position(this.x + x, this.y + y);
    }

    public Position add(Position pos) {
        Arguments.requireNonNull(pos, "pos");
        return add(pos.getX(), pos.getY());
    }

    public double distance(Position pos) {
        Arguments.requireNonNull(pos, "pos");
        final int dx = x - pos.getX();
        final int dy = y - pos.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
