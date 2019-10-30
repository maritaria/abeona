package abeona.aspects;

import abeona.util.Arguments;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BiFunctionTap<T, U, R> extends AbstractTap<BiFunctionTap.Interceptor<T, U, R>> implements BiFunction<T, U, R> {
    private final BiFunction<T, U, R> original;

    public BiFunctionTap(BiFunction<T, U, R> original) {
        Arguments.requireNonNull(original, "original");
        this.original = original;
    }

    @Override
    public R apply(T t, U u) {
        return invokeRecursive(t, u, handlers.iterator());
    }

    private R invokeRecursive(T t, U u, Iterator<Interceptor<T, U, R>> handlers) {
        if (handlers.hasNext()) {
            final var handler = handlers.next();
            return handler.intercept(t, u, (t2, u2) -> invokeRecursive(t2, u2, handlers));
        }
        return original.apply(t, u);
    }

    public interface Interceptor<T, U, R> {
        R intercept(T t, U u, BiFunction<T, U, R> next);
    }
}
