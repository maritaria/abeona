package abeona.demos.knapsack;

import abeona.util.Arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class KnapsackFilling {
    private final KnapsackPuzzle puzzle;
    private Collection<Item> items;

    public KnapsackFilling(KnapsackPuzzle puzzle, Collection<Item> items) {
        Arguments.requireNonNull(puzzle, "puzzle");
        this.puzzle = puzzle;
        this.items = new ArrayList<>(items);
    }

    public Stream<Item> getItems() {
        return items.stream();
    }

    public int totalWeight() {
        return items.stream().mapToInt(Item::getWeight).sum();
    }

    public int totalValue() {
        return items.stream().mapToInt(Item::getValue).sum();
    }

    public Stream<KnapsackFilling> next() {
        return puzzle.availableItems.stream().filter(item -> !this.items.contains(item)).map(item -> {
            final var extended = new ArrayList<>(items);
            extended.add(item);
            return new KnapsackFilling(puzzle, extended);
        }).filter(filling -> filling.totalWeight() <= puzzle.capacity);
    }

    @Override
    public int hashCode() {
        int hash = items.size();
        for (final var item : items) {
            hash ^= item.hashCode();
            hash = hash >>> 1;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KnapsackFilling) {
            final var other = (KnapsackFilling) obj;
            return items.size() == other.items.size() && items.containsAll(other.items);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "$" + totalValue() + " @ " + totalWeight() + "kg: [" + items.stream().map(Item::toString).collect(
                Collectors.joining(", ")) + "]";
    }
}
