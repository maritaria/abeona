package abeona.util;

import java.util.Iterator;
import java.util.function.Function;

public class MappingIterator<Source, Target> implements Iterator<Target> {
    private final Iterator<Source> sourceIterator;
    private final Function<Source, Target> transformer;

    public MappingIterator(Iterator<Source> sourceIterator, Function<Source, Target> transformer) {
        Arguments.requireNonNull(sourceIterator, "sourceIterator");
        Arguments.requireNonNull(transformer, "transformer");
        this.sourceIterator = sourceIterator;
        this.transformer = transformer;
    }

    @Override
    public boolean hasNext() {
        return sourceIterator.hasNext();
    }

    @Override
    public Target next() {
        final var sourceValue = sourceIterator.next();
        return transformer.apply(sourceValue);
    }

    @Override
    public void remove() {
        sourceIterator.remove();
    }
}
