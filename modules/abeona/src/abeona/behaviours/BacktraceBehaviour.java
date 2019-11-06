package abeona.behaviours;

import abeona.Query;
import abeona.State;
import abeona.Transition;
import abeona.TransitionEvaluationEvent;
import abeona.util.Arguments;

import java.util.*;

public class BacktraceBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final WeakHashMap<StateType, StateType> traceLinks = new WeakHashMap<>();
    private int mutationCounter = 0;

    @Override
    public void attach(Query<StateType> query) {
        Arguments.requireNonNull(query, "explorationQuery");
        tapQueryBehaviour(query, query.onStateDiscovery, this::onStateDiscovered);
    }

    public void attach(Query<StateType> query, TraceCostBehaviour<StateType> traceCostBehaviour) {
        Arguments.requireNonNull(query, "explorationQuery");
        Arguments.requireNonNull(traceCostBehaviour, "traceCostBehaviour");
        attach(query);
        tapForeignBehaviour(traceCostBehaviour.onLowerPathFound, this::onStateDiscovered);
    }

    private void onStateDiscovered(TransitionEvaluationEvent<StateType> event) {
        setTraceLink(event.getTransition());
    }

    public Optional<StateType> getTraceLink(StateType state) {
        Arguments.requireNonNull(state, "state");
        return Optional.ofNullable(traceLinks.get(state));
    }

    public void setTraceLink(Transition<StateType> transition) {
        Arguments.requireNonNull(transition, "transition");
        traceLinks.put(transition.getTargetState(), transition.getSourceState());
    }

    public Iterator<StateType> iterateBackwardsTrace(StateType traceEnd) {
        Arguments.requireNonNull(traceEnd, "traceEnd");
        return new TraceIterator(traceEnd, mutationCounter);
    }

    private class TraceIterator implements Iterator<StateType> {
        private final int expectedMutationCounter;
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private Optional<StateType> next;

        TraceIterator(StateType start, int expectedMutationCounter) {
            this.expectedMutationCounter = expectedMutationCounter;
            next = Optional.of(start);
        }

        @Override
        public boolean hasNext() {
            return next.isPresent();
        }

        @Override
        public StateType next() {
            final var result = next.orElseThrow(NoSuchFieldError::new);
            assertNoMutation();
            next = getTraceLink(result);
            return result;
        }

        private void assertNoMutation() {
            if (mutationCounter != expectedMutationCounter) {
                throw new ConcurrentModificationException("The underlying link store was mutated while iterating over this trace");
            }
        }
    }
}
