package abeona.heaps;

import abeona.util.Arguments;

import java.util.Iterator;

/**
 * Specifies that the heap implementation can be manipulated on a state level.
 * The heap is able to tell what states are contained inside of it exactly as well as remove specific states from itself
 *
 * @param <StateType>
 */
public interface ManagedHeap<StateType> extends Heap<StateType>, Iterable<StateType> {
    /**
     * Removes a particular state from the heap (if the heap contains it).
     *
     * @param state The state to remove from the heap
     * @return True if the state was in the heap and has been successfully removed, false otherwise
     */
    boolean remove(StateType state);

    /**
     * Returns an iterator that iterates over all states contained in the heap.
     * It is expected for the Iterator to support the remove() operation.
     *
     * @return An iterator that iterates over each state in the heap instance, supports remove()
     */
    @Override
    Iterator<StateType> iterator();

    @Override
    default boolean contains(StateType state) {
        Arguments.requireNonNull(state, "state");
        for (StateType knownState : this) {
            if (knownState.equals(state)) {
                return true;
            }
        }
        return false;
    }

    default long size() {
        long counter = 0;
        for (StateType stateType : this) {
            counter++;
        }
        return counter;
    }
}
