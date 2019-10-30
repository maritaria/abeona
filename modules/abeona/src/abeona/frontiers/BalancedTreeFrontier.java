package abeona.frontiers;

import abeona.State;
import abeona.util.Arguments;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// TODO: Add auto rebalancing

public class BalancedTreeFrontier<StateType extends State> implements DynamicallyOrderedFrontier<StateType> {
    private final Comparator<StateType> internalComparator;
    private int mutationCounter = 0;
    private final HandleGroup root = new HandleGroup();
    private int size = 0;

    @Override
    public Comparator<StateType> comparator() {
        return internalComparator;
    }

    public BalancedTreeFrontier(Comparator<StateType> comparator) {
        Objects.requireNonNull(comparator, "comparator is null");
        this.internalComparator = comparator;
    }

    @Override
    public void mutateOrderedProperty(StateType handle, Consumer<StateType> mutator) {
        // TODO: Improve by first finding the state, then mutating and then walk the tree from the known position
        boolean removed = root.remove(handle);
        try {
            mutator.accept(handle);
        } finally {
            if (removed) {
                root.insert(handle);
            }
        }
    }

    public boolean add(StateType state) {
        if (root.insert(state)) {
            size++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(StateType item) {
        if (root.remove(item)) {
            size--;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        root.lower = null;
        root.higher = null;
        root.states.clear();
        size = 0;
    }

    @Override
    public Iterator<StateType> iterator() {
        // TODO: Verify working of iterator().remove()
        return new GroupStatesIterator(mutationCounter, root);
    }

    @Override
    public Spliterator<StateType> spliterator() {
        return new TreeSpliterator(new GroupStatesIterator(mutationCounter, root));
    }

    @Override
    public boolean hasNext() {
        return !root.states.isEmpty();
    }

    @Override
    public StateType next() {
        var current = root;
        while (current.lower != null) {
            current = current.lower;
        }
        final var iterator = current.states.iterator();
        final var result = iterator.next();
        iterator.remove();
        return result;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    private class HandleGroup {
        Set<StateType> states = new HashSet<>();
        HandleGroup lower, higher;

        boolean insert(StateType item) {
            if (states.isEmpty()) {
                assert lower == null : "lower is not null while states is empty";
                assert higher == null : "higher is not null while states is empty";
                return states.add(item);
            }
            final StateType first = states.iterator().next();
            final int comp = internalComparator.compare(item, first);
            if (comp < 0) {
                if (lower == null) {
                    lower = new HandleGroup();
                }
                return lower.insert(item);
            } else if (comp > 0) {
                if (higher == null) {
                    higher = new HandleGroup();
                }
                return higher.insert(item);
            } else /* implied: comp == 0 */ {
                return states.add(item);
            }
        }

        boolean remove(StateType item) {
            if (states.isEmpty()) {
                assert lower == null : "lower is not null while states is empty";
                assert higher == null : "higher is not null while states is empty";
                return false;
            }
            boolean result = false;
            final StateType first = states.iterator().next();
            final int comp = internalComparator.compare(item, first);
            if (comp < 0) {
                if (lower != null) {
                    result = lower.remove(item);
                    if (lower.states.isEmpty()) {
                        lower = null;
                    }
                }
            } else if (comp > 0) {
                if (higher != null) {
                    result = higher.remove(item);
                    if (higher.states.isEmpty()) {
                        higher = null;
                    }
                }
            } else /* implied: comp == 0 */ {
                result = states.remove(item);
                this.rebalance();
            }
            return result;
        }

        void rebalance() {
            if (states.isEmpty()) {
                if (lower == null) {
                    if (higher != null) {
                        this.states = higher.states;
                        this.lower = higher.lower;
                        this.higher = higher.higher;
                    }
                } else {
                    if (higher == null) {
                        this.states = lower.states;
                        this.higher = lower.higher;
                        this.lower = lower.lower;
                    } else {
                        // lower and higher exist
                        if (this.lower.states.size() > this.higher.states.size()) {
                            this.states = lower.steal();
                            lower.rebalance();
                        } else {
                            this.states = higher.steal();
                            higher.rebalance();
                        }
                    }
                }
            }
        }

        Set<StateType> steal() {
            final var result = this.states;
            this.states = Collections.emptySet();
            return result;
        }

        @Override
        public String toString() {
            ArrayList<String> items = new ArrayList<>();
            if (lower != null) {
                items.add(lower.toString());
            }
            states.stream()
                    .map(Object::toString)
                    .forEach(items::add);
            if (higher != null) {
                items.add(higher.toString());
            }
            return items.stream()
                    .collect(Collectors.joining(", ", "[ ", " ]"));
        }
    }

    private class HandleGroupIterator implements Iterator<HandleGroup> {
        private final static int STATE_START = 0;
        private final static int STATE_LOWER = 1;
        private final static int STATE_NODE = 2;
        private final static int STATE_RIGHT = 3;

        private HandleGroup node;
        private int state;
        private Iterator<HandleGroup> inner;

        public HandleGroupIterator(HandleGroup node) {
            Objects.requireNonNull(node, "node is null");
            this.node = node;
            this.state = STATE_START;
            this.inner = Collections.emptyIterator();
        }

        @Override
        public boolean hasNext() {
            if (inner.hasNext()) {
                return true;
            } else {
                switch (state) {
                    case STATE_START:
                        state = STATE_LOWER;
                        if (node.lower != null) {
                            inner = new HandleGroupIterator(node.lower);
                            break;
                        }
                    case STATE_LOWER:
                        state = STATE_NODE;
                        inner = Collections.singleton(node).iterator();
                        break;
                    case STATE_NODE:
                        state = STATE_RIGHT;
                        if (node.higher != null) {
                            inner = new HandleGroupIterator(node.higher);
                            break;
                        }
                    default:
                        state = STATE_RIGHT;
                        inner = Collections.emptyIterator();
                        return false;
                }
                return hasNext();
            }
        }

        @Override
        public HandleGroup next() {
            if (hasNext()) {
                return inner.next();
            } else {
                throw new NoSuchElementException();
            }
        }
    }

    private class GroupStatesIterator implements Iterator<StateType> {
        // TODO: Merge with NestedIterator
        private int currentMutation;
        private HandleGroupIterator groupIterator;
        private Iterator<StateType> handleIterator;
        private StateType peekedHandle;
        private StateType removeHandle;

        public GroupStatesIterator(int currentMutation, HandleGroup root) {
            Objects.requireNonNull(root, "root is null");
            this.currentMutation = currentMutation;
            groupIterator = new HandleGroupIterator(root);
            handleIterator = Collections.emptyIterator();
        }

        private boolean prepareNextGroup() {
            if (groupIterator.hasNext()) {
                handleIterator = groupIterator.next().states.iterator();
                return true;
            } else {
                return false;
            }
        }

        private boolean prepareNextHandle() {
            if (handleIterator.hasNext()) {
                peekedHandle = handleIterator.next();
                return true;
            } else if (prepareNextGroup()) {
                return prepareNextHandle();
            } else {
                return false;
            }
        }

        @Override
        public boolean hasNext() {
            if (BalancedTreeFrontier.this.mutationCounter != this.currentMutation) {
                throw new ConcurrentModificationException("Underlaying RedBlackTreeFrontier was modified during iteration");
            }
            return prepareNextHandle();
        }

        @Override
        public StateType next() {
            if (peekedHandle != null) {
                final var result = peekedHandle;
                peekedHandle = null;
                removeHandle = result;
                return result;
            } else if (hasNext()) {
                return next();
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            if (removeHandle != null) {
                removeHandle = null;
                root.remove(peekedHandle);
                currentMutation = mutationCounter;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    private class TreeSpliterator extends Spliterators.AbstractSpliterator<StateType> {
        private static final int FLAGS = Spliterator.DISTINCT | Spliterator.ORDERED | Spliterator.SORTED | Spliterator.SIZED;
        private final GroupStatesIterator iterator;

        protected TreeSpliterator(GroupStatesIterator iterator) {
            super(size, FLAGS);
            Arguments.requireNonNull(iterator, "iterator");
            this.iterator = iterator;
        }

        @Override
        public boolean tryAdvance(Consumer<? super StateType> consumer) {
            if (!iterator.hasNext()) {
                return false;
            }
            consumer.accept(iterator.next());
            return true;
        }

        @Override
        public Comparator<? super StateType> getComparator() {
            return internalComparator;
        }
    }
}
