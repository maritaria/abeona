package abeona.frontiers;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Defines a {@link ManagedFrontier} that is ordered by a known comparator.
 * This frontier assumes that the source value of the comparator are known at insertion-time and are immutable.
 * This means you cannot use a comparator that sorts on a property of the states that may mutate during exploration.
 * For that case use the {@link DynamicallyOrderedFrontier} instead.
 * @param <StateType>
 */
public interface OrderedFrontier<StateType> extends ManagedFrontier<StateType> {
    /**
     * Gets the comparator used to sort this frontier.
     * Implementations should not return null.
     * @return
     */
    Comparator<StateType> comparator();

    /**
     * Peeks the lowest-valued state in the frontier.
     * Behaviour is undefined if there are multiple states that are considered equal by the comparator but lesser than all other states in the frontier.
     * If the frontier has only one state then no comparison is performed and that single state is peeked.
     * @return An optional holding the lowest-valued state in the frontier, or empty if the frontier is empty.
     */
    default Optional<StateType> peekLast() {
        return stream().min(comparator());
    }

    default StateType removeLast() {
        final var back = peekLast()
                .orElseThrow(NoSuchElementException::new);
        remove(back);
        return back;
    }
}
