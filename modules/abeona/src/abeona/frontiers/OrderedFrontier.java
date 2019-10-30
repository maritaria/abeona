package abeona.frontiers;

import abeona.State;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface OrderedFrontier<StateType extends State> extends ManagedFrontier<StateType> {
    Comparator<StateType> comparator();

    default Optional<StateType> peekLast() {
        return stream().min(comparator());
    }

    default StateType removeLast() {
        final var back = peekLast()
                .orElseThrow(NoSuchElementException::new);
        remove(back);
        return back;
    }
}
