package abeona.util;


import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Iterator that wraps another iterator and can flatten its elements
 * @param <Source>
 * @param <Target>
 */
public class FlatMapIterator<Source, Target> implements Iterator<Target> {
    private final Iterator<Source> sourceIterator;
    private final Function<Source, Iterator<Target>> childFinder;
    private Iterator<Target> childIterator;
    private Target peeked;

    public FlatMapIterator(Iterator<Source> sourceIterator, Function<Source, Iterator<Target>> childFinder) {
        Arguments.requireNonNull(sourceIterator, "sourceIterator");
        Arguments.requireNonNull(childFinder, "childFinder");
        this.sourceIterator = sourceIterator;
        this.childFinder = childFinder;
        this.childIterator = Collections.emptyIterator();
    }

    private boolean prepareNextSource() {
        if (sourceIterator.hasNext()) {
            childIterator = childFinder.apply(sourceIterator.next());
            return true;
        } else {
            return false;
        }
    }

    private boolean prepareNextChild() {
        if (childIterator.hasNext()) {
            peeked = childIterator.next();
            return true;
        } else if (prepareNextSource()) {
            return prepareNextChild();
        } else {
            return false;
        }
    }

    @Override
    public boolean hasNext() {
        return prepareNextChild();
    }

    @Override
    public Target next() {
        if (peeked != null) {
            final var result = peeked;
            peeked = null;
            return result;
        } else if (hasNext()) {
            return next();
        } else {
            throw new NoSuchElementException();
        }
    }
}
