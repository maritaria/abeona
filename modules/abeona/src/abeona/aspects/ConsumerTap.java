package abeona.aspects;

import abeona.util.Arguments;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A consumer tap is a interceptor-style tap.
 * An interceptor allows for {@link ConsumerTap.Interceptor} callbacks to be registered.
 * These interceptors are executed in LIFO order.
 * Each interceptor receives the arguments passed to the {@link #accept(Object)} function and is able to control the invocation and return result of the next interceptor.
 * The first registered interceptor receives the original consumer as its next-function.
 * @param <T>
 */
public class ConsumerTap<T> extends AbstractTap<ConsumerTap.Interceptor<T>> implements Consumer<T> {
    private final Consumer<T> original;

    /**
     * Create a new interceptable consumer
     * @param original The original consumer logic
     * @throws IllegalArgumentException Thrown if the original consumer is null
     */
    public ConsumerTap(Consumer<T> original) {
        Arguments.requireNonNull(original, "original");
        this.original = original;
    }

    @Override
    public void accept(T t) {
        invokeRecursive(t, handlers.iterator());
    }

    private void invokeRecursive(T t, Iterator<Interceptor<T>> handlers) {
        if (handlers.hasNext()) {
            final var handler = handlers.next();
            handler.intercept(t, t2 -> invokeRecursive(t2, handlers));
            return;
        }
        original.accept(t);
    }

    public interface Interceptor<T> {
        /**
         * Called when the {@link ConsumerTap#accept(Object)} is executed.
         * An interceptor is powerful in that it can fully control the execution conditions of the original behaviour.
         * The original behaviour doesn't need to be executed at all should the interceptor decide so.
         * @param t The value on which the operation should be performed
         * @param next The original consumer that performs the operation. This may also invoke the next interceptor in the queue.
         */
        void intercept(T t, Consumer<T> next);
    }
}
