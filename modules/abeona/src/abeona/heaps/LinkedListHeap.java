package abeona.heaps;

import java.util.Iterator;
import java.util.LinkedList;

public class LinkedListHeap<StateType> implements ManagedHeap<StateType> {
    private final LinkedList<StateType> states = new LinkedList<>();

    @Override
    public boolean remove(StateType state) {
        return states.remove(state);
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

    @Override
    public boolean add(StateType state) {
        return states.add(state);
    }

    @Override
    public void clear() {
        states.clear();
    }
}
