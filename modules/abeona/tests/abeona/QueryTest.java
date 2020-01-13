package abeona;

import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class QueryTest {
    private static final class MyState {
        private final int hash;

        MyState(final int hash) {
            this.hash = hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof MyState) {
                final var other = (MyState) obj;
                return hash == other.hash;
            } else {
                return false;
            }
        }

        Stream<MyState> next() {
            return Stream.of(new MyState(hash + 1), new MyState(hash * 2));
        }
    }

    @Test
    void constructor() {
        fail("Test not implemented");
    }

    @Test
    void getFrontier() {
        fail("Test not implemented");
    }

    @Test
    void getHeap() {
        fail("Test not implemented");
    }

    @Test
    void explore() {
        fail("Test not implemented");
    }

    @Test
    void evaluateState() {
        fail("Test not implemented");
    }

    @Test
    void addBehaviour() {
        fail("Test not implemented");
    }

    @Test
    void removeBehaviour() {
        fail("Test not implemented");
    }

    @Test
    void getBehaviours() {
        fail("Test not implemented");
    }

    @Test
    void testGetBehaviours() {
        fail("Test not implemented");
    }

    @Test
    void internState() {
        final var query = new Query<MyState>(
                QueueFrontier.<MyState>fifoFrontier(),
                new HashSetHeap<>(),
                state -> state.next().map(next -> new Transition<>(state, next))
        );

        final var state1 = new MyState(1);
        final var state1Copy = new MyState(state1.hash);
        final var state2 = new MyState(2);

        assertSame(state1, query.internState(state1));
        assertSame(state1, query.internState(state1Copy));
        assertSame(state2, query.internState(state2));
        assertSame(state1, query.internState(state1));
        assertSame(state1, query.internState(state1Copy));
        assertSame(state2, query.internState(state2));
    }
}