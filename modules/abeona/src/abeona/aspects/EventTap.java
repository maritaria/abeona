package abeona.aspects;

import abeona.util.Arguments;

import java.util.ConcurrentModificationException;
import java.util.function.Consumer;

/**
 * Generic {@link Tap} implementation for events.
 * The tap allows for listening to events that emit a specific type of data.
 * The invocation order of the registered handlers is registration order.
 *
 * @param <EventData> The type for event data
 */
public class EventTap<EventData> extends AbstractTap<Consumer<EventData>> implements Consumer<EventData> {
    /**
     * Invoke all registered handlers, in order of registration, with the data provided.
     * There is no error handling, meaning that an error thrown in one of the handlers prevents the remaining handlers from being invoked and the error travels up to the caller.
     *
     * @param data The data for the event to pass to each handler, null not allowed.
     *
     * @throws IllegalArgumentException If the {@code data} argument is {@code null}.
     * @throws ConcurrentModificationException If the underlying handler store is modified by one of the invoked handlers.
     */
    public void accept(EventData data) {
        Arguments.requireNonNull(data, "data");
        final var iterator = handlers.descendingIterator();
        while (iterator.hasNext()) {
            final var handler = iterator.next();
            handler.accept(data);
        }
    }
}
