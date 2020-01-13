package abeona.demos.maze;

import abeona.Transition;
import abeona.util.Arguments;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PlayerState {
    private final Maze.Cell location;

    public PlayerState(Maze.Cell location) {
        Arguments.requireNonNull(location, "location");
        this.location = location;
    }

    public Maze.Cell getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerState) {
            final var other = (PlayerState) obj;
            return location == other.location;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return location.getPos().hashCode();
    }

    @Override
    public String toString() {
        return location.getPos().toString();
    }

    public Stream<Transition<PlayerState>> next() {
        final var iterator = new NextIterator(this);
        final int characteristics = Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.DISTINCT;
        final var spliterator = Spliterators.spliteratorUnknownSize(iterator, characteristics);
        return StreamSupport.stream(spliterator, false).map(next -> new Transition<>(this, next));
    }

    static class NextIterator implements Iterator<PlayerState> {
        private final PlayerState source;
        private Phase phase = Phase.Left;
        private PlayerState prepared = null;

        NextIterator(PlayerState source) {
            Arguments.requireNonNull(source, "source");
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            final var cell = source.getLocation();
            if (prepared == null) {
                switch (phase) {
                    case Left:
                        phase = Phase.Right;
                        if (!cell.isWallLeft()) {
                            prepared = new PlayerState(cell.getLeft().orElseThrow());
                            break;
                        }
                    case Right:
                        phase = Phase.Up;
                        if (!cell.isWallRight()) {
                            prepared = new PlayerState(cell.getRight().orElseThrow());
                            break;
                        }
                    case Up:
                        phase = Phase.Down;
                        if (!cell.isWallTop()) {
                            prepared = new PlayerState(cell.getTop().orElseThrow());
                            break;
                        }
                    case Down:
                        phase = Phase.Done;
                        if (!cell.isWallBottom()) {
                            prepared = new PlayerState(cell.getBottom().orElseThrow());
                            break;
                        }
                    case Done:
                        break;
                }
            }
            return prepared != null;
        }

        @Override
        public PlayerState next() {
            if (prepared == null) {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
            }
            final var result = prepared;
            prepared = null;
            return result;
        }

        enum Phase {
            Left, Right, Up, Down, Done
        }
    }
}
