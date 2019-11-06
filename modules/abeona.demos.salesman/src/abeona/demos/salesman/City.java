package abeona.demos.salesman;

import abeona.util.Arguments;

public final class City {
    private final String name;
    private final Coordinate pos;

    public City(String name, int x, int y) {
        Arguments.requireNonNull(name, "name");
        this.name = name;
        this.pos = new Coordinate(x, y);
    }

    public String getName() {
        return name;
    }

    public Coordinate getPos() {
        return pos;
    }

    public double distance(City other) {
        Arguments.requireNonNull(other, "other");
        return pos.distance(other.pos);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ pos.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof City) {
            final var other = (City) obj;
            return name.equals(other.name) && pos.equals(other.pos);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name + "@" + pos.toString();
    }
}
