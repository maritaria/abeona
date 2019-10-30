package abeona.frontiers;

import abeona.State;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class QueueFrontier<StateType extends State> implements ManagedFrontier<StateType> {
    private final ArrayDeque<StateType> queue = new ArrayDeque<>();
    private final Consumer<StateType> addOperation;

    private QueueFrontier(boolean isStack) {
        this.addOperation = isStack ? queue::addFirst : queue::addLast;
    }

    @Override
    public boolean add(StateType state) {
        if (!queue.contains(state)) {
            this.addOperation.accept(state);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public StateType next() {
        return queue.pop();
    }

    @Override
    public boolean remove(StateType item) {
        return queue.remove(item);
    }

    @Override
    public Iterator<StateType> iterator() {
        return queue.iterator();
    }

    public static <StateType extends State> QueueFrontier<StateType> fifoFrontier() {
        return new QueueFrontier<>(false);
    }

    public static <StateType extends State> QueueFrontier<StateType> lifoFrontier() {
        return new QueueFrontier<>(true);
    }
}
