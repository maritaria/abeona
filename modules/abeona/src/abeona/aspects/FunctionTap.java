package abeona.aspects;

import abeona.util.Arguments;

import java.util.Iterator;
import java.util.function.Function;

/**
 * A function tap is a interceptor-style tap.
 * An interceptor allows for {@link FunctionTap.Interceptor} callbacks to be registered.
 * These interceptors are executed in LIFO order.
 * Each interceptor receives the arguments passed to the {@link #apply(Object)} function and is able to control the invocation and return result of the next interceptor.
 * The first registered interceptor receives the original function as its next-function.
 * @param <T>
 * @param <R>
 */
public class FunctionTap<T, R> extends AbstractTap<FunctionTap.Interceptor<T, R>> implements Function<T, R> {
    private final Function<T, R> original;

    /**
     * Create an interceptable function
     * @param original The original function to allow interception on
     * @throws IllegalArgumentException Thrown if the original function is null
     */
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
        /**
         * Called when the {@link FunctionTap#apply(Object)} is executed.
         * An interceptor is powerful in that it can fully control the execution conditions of the original behaviour.
         * The original behaviour doesn't need to be executed at all should the interceptor decide so.
         * @param t The value on which the operation should be performed
         * @param next The original function that performs the operation. This may also invoke the next interceptor in the queue.
         * @return
         */
        R intercept(T t, Function<T, R> next);
    }
}
