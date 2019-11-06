package abeona.behaviours;

import abeona.Query;
import abeona.State;
import abeona.StateEvent;
import abeona.aspects.EventTap;
import abeona.heaps.ManagedHeap;
import abeona.util.Arguments;

import java.util.Comparator;
import java.util.function.Consumer;

public final class SweepLineBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final Comparator<StateType> progressComparator;
    public final EventTap<StateEvent<StateType>> onPurge = new EventTap<>();

    public SweepLineBehaviour(Comparator<StateType> progressComparator) {
        Arguments.requireNonNull(progressComparator, "progressComparator");
        this.progressComparator = progressComparator;
    }

    @Override
    public void attach(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        Arguments.requireInstanceOf(query.getHeap(), ManagedHeap.class, "query.heap");
        final var heap = (ManagedHeap<StateType>) query.getHeap();
        tapQueryBehaviour(query, query.afterStatePicked, new HeapPurger(heap));
    }

    private class HeapPurger implements Consumer<StateEvent<StateType>> {
        private final ManagedHeap<StateType> heap;

        HeapPurger(ManagedHeap<StateType> heap) {
            Arguments.requireNonNull(heap, "heap");
            this.heap = heap;
        }

        @Override
        public void accept(StateEvent<StateType> event) {
            final var threshold = event.getState();
            final var iterator = heap.iterator();
            while (iterator.hasNext()) {
                final var state = iterator.next();
                if (progressComparator.compare(state, threshold) < 0) {
                    iterator.remove();
                    onPurge.accept(event);
                }
            }
        }
    }
}
