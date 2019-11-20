package abeona.aspects;

import abeona.util.Arguments;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A function tap is a interceptor-style tap.
 * An interceptor allows for {@link BiFunctionTap.Interceptor} callbacks to be registered.
 * These interceptors are executed in LIFO order.
 * Each interceptor receives the arguments passed to the {@link #apply(Object, Object)} function and is able to control the invocation and return result of the next interceptor.
 * The first registered interceptor receives the original function as its next-function.
 * @param <T>
 * @param <R>
 */
public class BiFunctionTap<T, U, R> extends AbstractTap<BiFunctionTap.Interceptor<T, U, R>> implements BiFunction<T, U, R> {
    private final BiFunction<T, U, R> original;

    /**
     * Create a new interceptable bi-function
     * @param original The original bi-function logic
     * @throws IllegalArgumentException Thrown if the original bi-function is null
     */
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
        /**
         * Called when the {@link BiFunctionTap#apply(Object, Object)} is executed.
         * An interceptor is powerful in that it can fully control the execution conditions of the original behaviour.
         * The original behaviour doesn't need to be executed at all should the interceptor decide so.
         * @param t The value on which the operation should be performed
         * @param next The original function that performs the operation. This may also invoke the next interceptor in the queue.
         * @return
         */
        R intercept(T t, U u, BiFunction<T, U, R> next);
    }
}
