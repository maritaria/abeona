package abeona.heaps;

/**
 * An interface to the most basic functionality of a heap.
 * States can be added, checked for presence and the entire heap can be cleared.
 * This heap implementation is most basic because it does not assume the underlaying storage mechanism is able to be iterated or constrained to be finite.
 * A heap is a set-like collection mechanism, it can contain any given state at most once.
 * @param <StateType>
 */
public interface Heap<StateType> {
    /**
     * Puts the state in the heap if it was not yet already.
     * @param state The state to add to the heap set, not allowed to be null.
     * @return True if the state was not yet in the heap and has been successfully added, false otherwise.
     */
    boolean add(StateType state);

    /**
     * Tests if that state is present in the heap, depending on the state's equal() implementation there may be collisions between distinct states that may be considered equivalent.
     * @param state The state to test presence for, not allowed to be null.
     * @return Returns true if the heap contains a state that is equal to the given state, false otherwise.
     */
    boolean contains(StateType state);

    /**
     * Removes all state presence information held within the heap.
     */
    void clear();
}
