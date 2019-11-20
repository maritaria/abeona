package abeona.frontiers;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Base interface defining the features of a frontier.
 *
 * The frontier behaves like an iterator itself, as requesting the next state from the frontier automatically moves it forward.
 * Additionally, the frontier can be extended by inserting streams of states.
 * Also, the frontier can be cleared.
 *
 * @param <StateType>
 */
public interface Frontier<StateType> extends Iterator<StateType> {
    /**
     * Adds a stream of states to the frontier.
     * The stream is not guaranteed to be evaluated at this point, a lazy-frontier may hold on to the streams and iterate over them.
     *
     * @param generator The stream of states to append to the frontier.
     * @return True if the frontier was modified, false otherwise.
     */
    boolean add(Stream<? extends StateType> generator);

    /**
     * Removes all pending states from the frontier.
     * After this method the frontier acts as an empty iterator until the {@link #add(Stream)} method is used to add new states again.
     */
    void clear();
}
