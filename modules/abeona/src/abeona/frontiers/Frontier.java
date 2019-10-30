package abeona.frontiers;

import java.util.Iterator;
import java.util.stream.Stream;

public interface Frontier<StateType> extends Iterator<StateType> {
    boolean add(Stream<? extends StateType> generator);

    void clear();
}
