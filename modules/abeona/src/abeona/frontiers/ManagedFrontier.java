package abeona.frontiers;

import abeona.State;
import abeona.util.Arguments;

import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ManagedFrontier<StateType extends State> extends Frontier<StateType>, Iterable<StateType> {
    boolean add(StateType state);

    boolean remove(StateType item);

    default Optional<StateType> peekNext() {
        final var iterator = iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    default boolean contains(StateType item) {
        Arguments.requireNonNull(item, "state");
        for (StateType knownState : this) {
            if (knownState.equals(item)) {
                return true;
            }
        }
        return false;
    }

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

    default Stream<StateType> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    default Spliterator<StateType> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.DISTINCT | Spliterator.NONNULL);
    }
}
