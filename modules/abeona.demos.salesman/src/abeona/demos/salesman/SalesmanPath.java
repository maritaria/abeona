package abeona.demos.salesman;

import abeona.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class SalesmanPath implements State {
    private final Collection<City> order;

    public SalesmanPath(Collection<City> cities) {
        final var list = new ArrayList<>(cities.size());
        for (final var city : cities) {
            if (list.contains(city)) {
                throw new IllegalArgumentException("Argument cities contains duplicate elements");
            }
            list.add(city);
        }

        this.order = Collections.unmodifiableCollection(cities);
    }

    public Stream<City> cities() {
        return order.stream();
    }

    public double getLength() {
        if (order.isEmpty()) {
            return 0;
        }
        double length = 0;
        final var iterator = order.iterator();
        final var first = iterator.next();
        var previous = first;
        while (iterator.hasNext()) {
            var current = iterator.next();
            length += previous.distance(current);
            previous = current;
        }
        length += previous.distance(first);
        return length;
    }


    public SalesmanPath swap(int first, int second) {
        first = normalizeIndex(first);
        second = normalizeIndex(second);
        if (first == second) {
            return this;
        }
        final var newOrder = new ArrayList<>(order);
        final var a = newOrder.get(first);
        final var b = newOrder.get(second);
        newOrder.set(first, b);
        newOrder.set(second, a);
        return new SalesmanPath(newOrder);
    }

    public Stream<SalesmanPath> next() {
        Random r = new Random(this.hashCode());

        return IntStream.range(1, order.size() - 1).mapToObj(i -> IntStream.range(i + 1, order.size())
                .mapToObj(j -> this.swap(i, j))).flatMap(Function.identity());
    }

    private int normalizeIndex(int index) {
        final int size = order.size();
        index = index % size;
        if (index < 0) {
            index += size;
        }
        return index;
    }

    @Override
    public int hashCode() {
        return order.stream().map(City::hashCode).reduce(0, (a, b) -> a ^ (b << 1) ^ (b >>> 31));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SalesmanPath) {
            final var other = (SalesmanPath) obj;
            final var myIter = order.iterator();
            final var otherIter = other.order.iterator();
            while (myIter.hasNext()) {
                if (!otherIter.hasNext()) {
                    return false;
                }
                final var myNext = myIter.next();
                final var otherNext = otherIter.next();
                if (!myNext.equals(otherNext)) {
                    return false;
                }
            }
            return !otherIter.hasNext();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return getLength() + ": [" + order.stream().map(City::getName).collect(Collectors.joining(", ")) + "]";
    }
}
