package abeona.heaps;

import abeona.State;

import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class NullHeap<StateType extends State> implements ManagedHeap<StateType> {
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
}
