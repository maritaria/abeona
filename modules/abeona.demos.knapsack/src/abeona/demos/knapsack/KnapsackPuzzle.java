package abeona.demos.knapsack;

import abeona.util.Arguments;

import java.util.HashSet;
import java.util.Set;

public final class KnapsackPuzzle {
    public final int capacity;
    public final Set<Item> availableItems = new HashSet<>();

    public KnapsackPuzzle(int capacity) {
        Arguments.requireMinimum(0, capacity, "capacity");
        this.capacity = capacity;
    }
}
