package abeona;

import abeona.util.Arguments;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Functional interface to the next-function.
 * A next-function is a function that for a given state is able to produce an enumeration of the outgoing transitions.
 *
 * There also is a helper function {@link #wrap(Function)} that creates a next-function given you can at least identify the neighbours of a given state.
 *
 * @param <StateType>
 */
@FunctionalInterface
public interface NextFunction<StateType> extends Function<StateType, Stream<Transition<StateType>>> {
    /**
     * Create a proper next-function given that you have a function that identifies the neighbours of a given state.
     * @param next The neighbour generator function
     * @param <StateType>
     * @return A next-function that produces a stream of transitions between the source state and the neighbour states identied by the neighbour generator function.
     */
    static <StateType> NextFunction<StateType> wrap(Function<StateType, Stream<StateType>> next) {
        Arguments.requireNonNull(next, "next");
        return source -> next.apply(source).map(target -> new Transition<>(source, target));
    }
}
