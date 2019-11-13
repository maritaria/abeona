package abeona.demos.maze;

import abeona.State;
import abeona.Transition;
import abeona.util.Arguments;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PlayerState implements State {
    private final Maze.Cell location;

    PlayerState(Maze.Cell location) {
        Arguments.requireNonNull(location, "location");
        this.location = location;
    }

    Maze.Cell getLocation() {
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
        final var iterator = new NextIterator(location);
        final int characteristics = Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.DISTINCT;
        final var spliterator = Spliterators.spliteratorUnknownSize(iterator, characteristics);
        return StreamSupport.stream(spliterator, false)
                .map(next -> new Transition<>(this, next));
    }

    static class NextIterator implements Iterator<PlayerState> {
        private final Maze.Cell source;
        private Phase phase = Phase.Left;
        private PlayerState prepared = null;

        NextIterator(Maze.Cell cell) {
            Arguments.requireNonNull(cell, "cell");
            this.source = cell;
        }

        @Override
        public boolean hasNext() {
            if (prepared == null) {
                switch (phase) {
                    case Left:
                        phase = Phase.Right;
                        if (!source.isWallLeft()) {
                            prepared = new PlayerState(source.getLeft().orElseThrow());
                            break;
                        }
                    case Right:
                        phase = Phase.Up;
                        if (!source.isWallRight()) {
                            prepared = new PlayerState(source.getRight().orElseThrow());
                            break;
                        }
                    case Up:
                        phase = Phase.Down;
                        if (!source.isWallTop()) {
                            prepared = new PlayerState(source.getTop().orElseThrow());
                            break;
                        }
                    case Down:
                        phase = Phase.Done;
                        if (!source.isWallBottom()) {
                            prepared = new PlayerState(source.getBottom().orElseThrow());
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
