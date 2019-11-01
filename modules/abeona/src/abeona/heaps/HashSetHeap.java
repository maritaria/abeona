package abeona.heaps;

import abeona.State;
import abeona.util.Arguments;
import abeona.util.MappingIterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class HashSetHeap<StateType extends State> implements ManagedHeap<StateType> {
    // TODO: Create two implementations, one with a wrapper and one without
    private final Set<StateHolder> states = new HashSet<>();
    private final BiPredicate<StateType, StateType> equivalence = Objects::equals;
    private final ToIntFunction<StateType> hasher = Objects::hashCode;

    @Override
    public boolean add(StateType state) {
        return states.add(new StateHolder(state));
    }

    public boolean remove(StateType state) {
        return states.remove(new StateHolder(state));
    }

    @Override
    public void clear() {
        states.clear();
    }

    @Override
    public Iterator<StateType> iterator() {
        return new MappingIterator<>(states.iterator(), StateHolder::getState);
    }

    @Override
    public boolean contains(StateType state) {
        return states.contains(new StateHolder(state));
    }

    private class StateHolder {
        private final StateType state;

        public StateType getState() {
            return state;
        }

        public StateHolder(StateType state) {
            Arguments.requireNonNull(state, "state");
            this.state = state;
        }

        @Override
        public int hashCode() {
            return HashSetHeap.this.hasher.applyAsInt(state);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof HashSetHeap.StateHolder) {
                final var other = (HashSetHeap.StateHolder) obj;
                // assert state.getClass().isAssignableFrom(other.state.getClass()) : "incompatible state types";
                @SuppressWarnings("unchecked") final var otherState = (StateType) other.state;
                return HashSetHeap.this.equivalence.test(state, otherState);
            } else {
                return false;
            }
        }
    }
}
