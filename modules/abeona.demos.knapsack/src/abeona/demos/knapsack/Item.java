package abeona.demos.knapsack;

public final class Item {
    private final int value, weight;

    public Item(int value, int weight) {
        this.value = value;
        this.weight = weight;
    }

    public int getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int hashCode() {
        return value ^ (weight >>> 4);
    }

    @Override
    public String toString() {
        return "$" + value + " @ " + weight + "kg";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            final var other = (Item) obj;
            return value == other.value && weight == other.weight;
        } else {
            return false;
        }
    }
}
