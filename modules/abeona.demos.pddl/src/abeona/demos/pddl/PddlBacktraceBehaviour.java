package abeona.demos.pddl;

import abeona.Query;
import abeona.Transition;
import abeona.TransitionEvaluationEvent;
import abeona.behaviours.AbstractBehaviour;
import abeona.util.Arguments;
import fr.uga.pddl4j.util.BitState;

import java.util.Iterator;
import java.util.Optional;
import java.util.WeakHashMap;

public final class PddlBacktraceBehaviour extends AbstractBehaviour<BitState> {
    private final WeakHashMap<BitState, Transition<BitState>> traceLinks = new WeakHashMap<>();

    @Override
    public void attach(Query<BitState> query) {
        this.tapQueryBehaviour(query, query.onStateDiscovery, this::onStateDiscovery);
    }

    private void onStateDiscovery(TransitionEvaluationEvent<BitState> event) {
        final var transition = event.getTransition();
        final var state = transition.getTargetState();

        traceLinks.put(state, transition);
    }

    public Optional<Transition<BitState>> getBacklink(BitState state) {
        return Optional.ofNullable(traceLinks.get(state));
    }

    public Iterator<Transition<BitState>> iterateBackwardsTrace(BitState endState) {
        return new BacktraceIterator(traceLinks.get(endState));
    }

    private class BacktraceIterator implements Iterator<Transition<BitState>> {
        private Transition<BitState> current;

        public BacktraceIterator(Transition<BitState> initial) {
            Arguments.requireNonNull(initial, "initial");
            this.current = initial;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Transition<BitState> next() {
            final var result = current;
            current = traceLinks.get(result.getSourceState());
            return result;
        }
    }
}
