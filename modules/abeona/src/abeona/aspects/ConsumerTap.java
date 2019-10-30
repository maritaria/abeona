package abeona.aspects;

import abeona.util.Arguments;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConsumerTap<T> extends AbstractTap<ConsumerTap.Interceptor<T>> implements Consumer<T> {
    private final Consumer<T> original;

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
        void intercept(T t, Consumer<T> next);
    }
}
