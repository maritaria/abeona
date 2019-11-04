import abeona.State;
import abeona.Transition;
import abeona.util.Arguments;

import java.util.*;
import java.util.stream.Stream;

public final class Network {
    private final Map<String, Station> stations = new HashMap<>();
    private final Set<Connection> connections = new HashSet<>();

    public Stream<Station> stations() {
        return stations.values().stream();
    }

    public Station addStation(String name) {
        return stations.computeIfAbsent(name, Station::new);
    }

    private Connection linkStations(Station a, Station b, int distance) {
        final var connection = new Connection(a, b, distance);
        connections.add(connection);
        return connection;
    }

    public Optional<Station> getStation(String name) {
        return Optional.ofNullable(stations.get(name));
    }

    public final class Station implements State {
        private final String name;

        Station(String name) {
            Arguments.requireNonNull(name, "name");
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Station createNeighbour(String name, int distance) {
            final var other = Network.this.addStation(name);
            Network.this.linkStations(this, other, distance);
            return other;
        }

        public Connection linkTo(Station other, int distance) {
            return Network.this.linkStations(this, other, distance);
        }

        public Stream<Connection> links() {
            return connections.stream()
                    .filter(c -> c.contains(this));
        }

        public Stream<Transition<Network.Station>> next() {
            return links().map(c -> new Transition<>(this, c.getOther(this), c.getDistance()));
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Station) {
                final var other = (Station) obj;
                return name.equals(other.name);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public final class Connection {
        private final Station a, b;
        private final int distance;

        public Connection(Station a, Station b, int distance) {
            Arguments.requireNonNull(a, "a");
            Arguments.requireNonNull(b, "b");
            Arguments.requireMinimum(1, distance, "distance");
            if (a.hashCode() > b.hashCode()) {
                final var temp = a;
                a = b;
                b = temp;
            }
            this.a = a;
            this.b = b;
            this.distance = distance;
        }

        public Station getA() {
            return a;
        }

        public Station getB() {
            return b;
        }

        public int getDistance() {
            return distance;
        }

        public Station getOther(Station station) {
            if (a == station) {
                return b;
            } else if (b == station) {
                return a;
            } else {
                throw new NoSuchElementException("The given station is not part of this connection");
            }
        }

        @Override
        public int hashCode() {
            return a.hashCode() ^ b.hashCode() ^ distance;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Connection) {
                final var other = (Connection) obj;
                return (a == other.a && b == other.b && distance == other.distance);
            } else {
                return false;
            }
        }

        boolean contains(Station station) {
            if (station == null) return false;
            return a.equals(station) || b.equals(station);
        }

        @Override
        public String toString() {
            return a.toString() + " <-> " + b.toString();
        }
    }
}
