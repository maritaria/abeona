package abeona.frontiers;

import abeona.State;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public class TreeMapFrontier<StateType extends State> implements DynamicallyOrderedFrontier<StateType> {
    private NavigableSet<StateType> tree;
    private Comparator<StateType> comparator;

    private TreeMapFrontier(Comparator<StateType> comparator) {
        Objects.requireNonNull(comparator, "comparator is null");
        // Since the used metric may find some states equal, we compare hashcodes for equal
        this.comparator = comparator;
        this.tree = new TreeSet<>(comparator);
    }

    public static <StateType extends State> TreeMapFrontier<StateType> withExactOrdering(Comparator<StateType> comparator) {
        return new TreeMapFrontier<>(comparator);
    }

    public static <StateType extends State> TreeMapFrontier<StateType> withCollisions(Comparator<StateType> sortComparer, ToIntFunction<StateType> equalityHasher) {
        return withExactOrdering(sortComparer.thenComparingInt(equalityHasher));
    }

    @Override
    public void mutateOrderedProperty(StateType state, Consumer<StateType> mutator) {
        boolean removed = tree.remove(state);
        try {
            mutator.accept(state);
        } finally {
            if (removed) {
                tree.add(state);
            }
        }
    }

    @Override
    public Comparator<StateType> comparator() {
        return comparator;
    }

    @Override
    public boolean add(StateType state) {
        return tree.add(state);
    }

    @Override
    public boolean remove(StateType item) {
        return tree.remove(item);
    }

    @Override
    public void clear() {
        tree.clear();
    }

    @Override
    public Iterator<StateType> iterator() {
        return tree.iterator();
    }

    @Override
    public boolean hasNext() {
        return !tree.isEmpty();
    }

    @Override
    public StateType next() {
        final var iterator = tree.iterator();
        final var result = iterator.next();
        iterator.remove();
        return result;
    }

    @Override
    public Optional<StateType> peekLast() {
        return tree.isEmpty() ? Optional.empty() : Optional.of(tree.last());
    }

    @Override
    public StateType removeLast() {
        final var iterator = tree.descendingIterator();
        final var result = iterator.next();
        iterator.remove();
        return result;
    }

    @Override
    public boolean contains(StateType item) {
        return tree.contains(item);
    }

    @Override
    public long size() {
        return tree.size();
    }
}
