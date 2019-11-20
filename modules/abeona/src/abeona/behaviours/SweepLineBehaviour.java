package abeona.behaviours;

import abeona.Query;
import abeona.State;
import abeona.StateEvent;
import abeona.aspects.EventTap;
import abeona.heaps.ManagedHeap;
import abeona.util.Arguments;

import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Realizes a sweep-line algorithm flow in a query.
 * Taps info {@link Query#afterStatePicked}.
 * Requires the heap to implement {@link ManagedHeap}
 *
 * After a state is picked the heap is purged of all states that are comparitively lower in progress according to a provided progress measure.
 * This behaviour assumes that the frontier of the query is sorted in accordance with the provided progress measure.
 *
 * This behaviour exposes a tappable event {@link #onPurge} to tap into the purging of states.
 *
 * @param <StateType>
 */
public final class SweepLineBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final Comparator<StateType> progressComparator;
    public final EventTap<StateEvent<StateType>> onPurge = new EventTap<>();

    /**
     * Creates a sweep-line purge behaviour that purges using the given comparator
     * @param progressComparator The progress comparator that is able to compare progression between states.
     *                           The comparator should indicate equivalence (0) for states with the same progress!
     */
    public SweepLineBehaviour(Comparator<StateType> progressComparator) {
        Arguments.requireNonNull(progressComparator, "progressComparator");
        this.progressComparator = progressComparator;
    }

    /**
     * @param query The instance to install logic into
     * @throws IllegalArgumentException Thrown if the given query is null or the query uses a heap which does not implement {@link ManagedHeap}
     */
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
