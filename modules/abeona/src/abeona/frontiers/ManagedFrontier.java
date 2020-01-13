package abeona.frontiers;

import abeona.util.Arguments;

import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Interface that indicates that a frontier is not a lazy-frontier and actually holds all the states.
 * The order of the frontier is not defined by this interface, it only allows for given states to be tested for presence, or be removed.
 * Since the frontier has knowledge of state presence it also should have the characteristics of a Set, as in that adding a already present state does not create duplicate entries.
 *
 * The interface provides some default implementations for its methods, it is encouraged to override them in derived classes/interfaces if the backing storage could lead to more efficient implementations of those methods.
 * The default implementations rely on the {@link #iterator()} to produce the values.
 *
 * @param <StateType>
 */
public interface ManagedFrontier<StateType> extends Frontier<StateType>, Iterable<StateType> {
    /**
     * Adds a single state to the frontier
     * @param state The state to add to the frontier
     * @return True if the frontier was modified, false otherwise
     */
    boolean add(StateType state);

    /**
     * Removes a state from the frontier
     * @param item The state to remove from the frontier
     * @return True if the frontier contained the state and was succesfully removed, false otherwise
     */
    boolean remove(StateType item);

    /**
     * Provides the next state in the frontier without removing it.
     * @return Optional holding the next state or empty optional if frontier is empty.
     */
    default Optional<StateType> peekNext() {
        final var iterator = iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    /**
     * Tests if a given state is present in the frontier.
     * @param state The state to test presence for
     * @return True if the state is present in the frontier, false otherwise
     * @throws IllegalArgumentException Thrown if the given state is null
     */
    default boolean contains(StateType state) {
        Arguments.requireNonNull(state, "state");
        for (StateType knownState : this) {
            if (knownState.equals(state)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the number of states in the frontier
     * @return The number of states in the frontier
     */
    default long size() {
        long counter = 0;
        final var iterator = iterator();
        while (iterator.hasNext()) {
            iterator.next();
            counter++;
        }
        return counter;
    }

    @Override
    default boolean add(Stream<? extends StateType> generator) {
        Arguments.requireNonNull(generator, "generator");
        return generator
                .map(this::add)
                .reduce(false, (a, b) -> a || b);
    }

    /**
     * Provides access to the frontiers items through a stream.
     * The stream does not support multi-threading.
     * The {@link #spliterator()} method is used to build the stream.
     * @return
     */
    default Stream<StateType> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    default Spliterator<StateType> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.DISTINCT | Spliterator.NONNULL);
    }
}
