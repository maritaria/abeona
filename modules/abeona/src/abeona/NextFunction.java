package abeona;

import abeona.util.Arguments;

import java.util.function.Function;
import java.util.stream.Stream;

public interface NextFunction<StateType extends State> extends Function<StateType, Stream<Transition<StateType>>> {
    static <StateType extends State> NextFunction<StateType> wrap(Function<StateType, Stream<StateType>> next) {
        Arguments.requireNonNull(next, "next");
        return source -> next.apply(source).map(target -> new Transition<>(source, target));
    }
}
