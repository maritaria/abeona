package abeona.frontiers;

import abeona.State;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A frontier implementation that orders its items based on discovery order.
 * The frontier must be constructed through either: {@link #fifoFrontier()} or {@link #lifoFrontier()} to create a fifo or lifo ordered frontier (respectively)
 * @param <StateType>
 */
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

    /**
     * Creates a frontier that orders its items based on first-in-first-out ordering, creates BFS exploration order.
     * @param <StateType>
     * @return
     */
    public static <StateType extends State> QueueFrontier<StateType> fifoFrontier() {
        return new QueueFrontier<>(false);
    }

    /**
     * Creates a frontier that orders its items based on last-in-first-out ordering, creates DFS exploration order.
     * @param <StateType>
     * @return
     */
    public static <StateType extends State> QueueFrontier<StateType> lifoFrontier() {
        return new QueueFrontier<>(true);
    }
}
