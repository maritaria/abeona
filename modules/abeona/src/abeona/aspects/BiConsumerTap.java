package abeona.aspects;

import abeona.util.Arguments;

import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BiConsumerTap<T, U> extends AbstractTap<BiConsumerTap.Interceptor<T, U>> implements BiConsumer<T, U> {
    private final BiConsumer<T, U> original;

    public BiConsumerTap(BiConsumer<T, U> original) {
        Arguments.requireNonNull(original, "original");
        this.original = original;
    }

    @Override
    public void accept(T t, U u) {
        invokeRecursive(t, u, handlers.iterator());
    }

    private void invokeRecursive(T t, U u, Iterator<Interceptor<T, U>> handlers) {
        if (handlers.hasNext()) {
            final var handler = handlers.next();
            handler.intercept(t, u, (t2, u2) -> invokeRecursive(t2, u2, handlers));
            return;
        }
        original.accept(t, u);
    }

    public interface Interceptor<T, U> {
        void intercept(T t, U u, BiConsumer<T, U> next);
    }
}
