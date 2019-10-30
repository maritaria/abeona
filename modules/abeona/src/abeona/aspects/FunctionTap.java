package abeona.aspects;

import abeona.util.Arguments;

import java.util.Iterator;
import java.util.function.Function;

public class FunctionTap<T, R> extends AbstractTap<FunctionTap.Interceptor<T, R>> implements Function<T, R> {
    private final Function<T, R> original;

    public FunctionTap(Function<T, R> original) {
        Arguments.requireNonNull(original, "original");
        this.original = original;
    }

    @Override
    public R apply(T t) {
        Arguments.requireNonNull(t, "t");
        return invokeRecursive(t, handlers.iterator());
    }

    private R invokeRecursive(T t, Iterator<Interceptor<T, R>> handlers) {
        if (handlers.hasNext()) {
            final var handler = handlers.next();
            return handler.intercept(t, t2 -> invokeRecursive(t2, handlers));
        }
        return original.apply(t);
    }

    @FunctionalInterface
    public interface Interceptor<T, R> {
        R intercept(T t, Function<T, R> next);
    }
}
