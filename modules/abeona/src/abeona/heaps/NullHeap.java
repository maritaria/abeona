package abeona.heaps;

import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

/**
 * A heap implementation that acts as /dev/null.
 * The heap never contains any state and any modification of the heap performs no operation and responds with no-modification where possible.
 * All instances of this class are considered equal and produce the same hashcode.
 * @param <StateType>
 */
public final class NullHeap<StateType> implements ManagedHeap<StateType> {
    @Override
    public boolean add(StateType state) {
        return false;
    }

    @Override
    public boolean remove(StateType state) {
        return false;
    }

    @Override
    public Iterator<StateType> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public void forEach(Consumer<? super StateType> action) {
    }

    @Override
    public Spliterator<StateType> spliterator() {
        return Spliterators.emptySpliterator();
    }

    @Override
    public boolean contains(StateType state) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullHeap;
    }

    @Override
    public String toString() {
        return "[NullHeap]";
    }

    @Override
    public long size() {
        return 0;
    }
}
