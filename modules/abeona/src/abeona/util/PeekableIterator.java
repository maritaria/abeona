package abeona.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Wraps an existing iterator with a single-value-cache so it can be peeked non-destructively.
 * This comes with the drawback that {@link Iterator#remove()} is no longer possible.
 * @param <T>
 */
public class PeekableIterator<T> implements Iterator<T> {
    private final Iterator<T> inner;
    private boolean peeked = false;
    private T peekedValue;

    public PeekableIterator(Iterator<T> inner) {
        Arguments.requireNonNull(inner, "inner");
        this.inner = inner;
    }

    @Override
    public boolean hasNext() {
        if (peeked) {
            return true;
        } else {
            return inner.hasNext();
        }
    }

    @Override
    public T next() {
        if (peeked) {
            peeked = false;
            return peekedValue;
        } else {
            return inner.next();
        }
    }

    public T peek() {
        if (peeked) {
            return peekedValue;
        } else if (hasNext()) {
            peeked = true;
            peekedValue = next();
            return peekedValue;
        } else {
            throw new NoSuchElementException();
        }
    }
}
