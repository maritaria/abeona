package abeona.aspects;

import abeona.util.Arguments;

import java.util.LinkedList;
import java.util.function.Predicate;

/**
 * Implements the {@link Tap} interface with mechanisms to register handlers in a {@link LinkedList}.
 * The handlers are marked with {@code protected}.
 * @param <Handler> The signature of the callback handlers.
 */
public abstract class AbstractTap<Handler> implements Tap<Handler> {
    /**
     * A linked list of the registered handlers. New handlers are inserted at the front.
     * To traverse the list in order of registration you need to traverse it back-to-front.
     */
    protected final LinkedList<Handler> handlers = new LinkedList<>();

    @Override
    public Handler tap(Handler handler) {
        Arguments.requireNonNull(handler, "handler");
        handlers.addFirst(handler);
        return handler;
    }

    @Override
    public void unTap(Handler handler) {
        Arguments.requireNonNull(handler, "handler");
        handlers.removeIf(Predicate.isEqual(handler));
    }

    /**
     * Removes all registered handlers from the tap.
     */
    public void unTapAll() {
        handlers.clear();
    }
}
