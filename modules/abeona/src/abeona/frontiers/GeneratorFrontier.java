package abeona.frontiers;

import abeona.State;
import abeona.util.PeekableIterator;

import java.util.*;
import java.util.stream.Stream;

/**
 * Implementation of a frontier that uses the streams as generators that lazily provide the states.
 * This frontier is not ordered and cannot guarantee that the states provided are not in the heap.
 * @param <StateType>
 */
public final class GeneratorFrontier<StateType extends State> implements Frontier<StateType> {
    private final Deque<Iterator<? extends StateType>> generators = new ArrayDeque<>();
    private Iterator<? extends StateType> currentGenerator = Collections.emptyIterator();

    @Override
    public boolean add(Stream<? extends StateType> generator) {
        generators.add(new PeekableIterator<>(generator.iterator()));
        return true;
    }

    @Override
    public void clear() {
        generators.clear();
        currentGenerator = Collections.emptyIterator();
    }

    private Iterator<? extends StateType> resolveCurrentGenerator() {
        if (currentGenerator.hasNext()) return currentGenerator;
        while (!generators.isEmpty() && !currentGenerator.hasNext()) {
            currentGenerator = generators.pop();
        }
        // Optimization: Release resources held by the tail generator that is kept just to maintain the hasNext() test
        if (!currentGenerator.hasNext()) {
            currentGenerator = Collections.emptyIterator();
        }
        return currentGenerator;
    }

    @Override
    public boolean hasNext() {
        return resolveCurrentGenerator().hasNext();
    }

    @Override
    public StateType next() {
        return resolveCurrentGenerator().next();
    }
}
