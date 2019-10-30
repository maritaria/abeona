package abeona.heaps;

import abeona.State;

public class NullHeap<StateType extends State> implements Heap<StateType> {
    @Override
    public boolean add(StateType state) {
        return false;
    }

    @Override
    public boolean contains(StateType state) {
        return false;
    }

    @Override
    public void clear() {
    }
}
