package abeona.heaps;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements a {@link HashSet} based heap.
 * All operations are funneled into a hashset.
 * The {@link StateType} is expected to override {@link Object#hashCode()} and {@link Object#equals(Object)} properly.
 *
 * @param <StateType>
 */
public class HashSetHeap<StateType> implements ManagedHeap<StateType> {
    // TODO: Create two implementations, one with a wrapper and one without
    private final Set<StateType> states = new HashSet<>();

    @Override
    public boolean add(StateType state) {
        return states.add(state);
    }

    public boolean remove(StateType state) {
        return states.remove(state);
    }

    @Override
    public void clear() {
        states.clear();
    }

    @Override
    public Iterator<StateType> iterator() {
        return states.iterator();
    }

    @Override
    public boolean contains(StateType state) {
        return states.contains(state);
    }

    @Override
    public long size() {
        return states.size();
    }
}
