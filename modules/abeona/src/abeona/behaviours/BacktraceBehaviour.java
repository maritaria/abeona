package abeona.behaviours;

import abeona.Query;
import abeona.State;
import abeona.Transition;
import abeona.TransitionEvaluationEvent;
import abeona.util.Arguments;

import java.util.*;

/**
 * Adds behaviour to the query to store links on states pointing to their discovering state.
 * This allows, after some exploration, for a trace to be build from any given state to an initial state.
 * Initial states do not have a back-pointer to another state.
 * @param <StateType>
 */
public class BacktraceBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final WeakHashMap<StateType, StateType> traceLinks = new WeakHashMap<>();
    private int mutationCounter = 0;

    @Override
    public void attach(Query<StateType> query) {
        Arguments.requireNonNull(query, "explorationQuery");
        tapQueryBehaviour(query, query.onStateDiscovery, this::onStateDiscovered);
    }

    /**
     * Attaches the behaviour with knowledge of updating backtraces when a cheaper path to a state is found during exploration.
     * This is needed when a {@link TraceCostBehaviour} is used in the query.
     * @param query
     * @param traceCostBehaviour
     * @throws IllegalArgumentException Thrown if an argument is null
     */
    public void attach(Query<StateType> query, TraceCostBehaviour<StateType> traceCostBehaviour) {
        Arguments.requireNonNull(query, "explorationQuery");
        Arguments.requireNonNull(traceCostBehaviour, "traceCostBehaviour");
        attach(query);
        tapForeignBehaviour(traceCostBehaviour.onLowerPathFound, this::onStateDiscovered);
    }

    private void onStateDiscovered(TransitionEvaluationEvent<StateType> event) {
        setTraceLink(event.getTransition());
    }

    /**
     * Gets the state (as an optional) that discovered the given state
     * @param state The state to get the source state for that discovered it.
     * @return Returns an optional holding the state that caused the given state to be known, or empty if the state does not have a known source.
     * @throws IllegalArgumentException Thrown if an argument is null
     */
    public Optional<StateType> getTraceLink(StateType state) {
        Arguments.requireNonNull(state, "state");
        return Optional.ofNullable(traceLinks.get(state));
    }

    /**
     * Registers a back-link through an existing transition.
     * The target state of the transition will have the source of the transition as its back-link.
     * @param transition
     * @throws IllegalArgumentException Thrown if an argument is null
     */
    public void setTraceLink(Transition<StateType> transition) {
        Arguments.requireNonNull(transition, "transition");
        traceLinks.put(transition.getTargetState(), transition.getSourceState());
    }

    /**
     * Get an iterator that follows the back-links starting at a given state.
     * The iterator works backwards to the initial state.
     * @param traceEnd The end-state to build a back-trace for.
     * @return
     * @throws IllegalArgumentException Thrown if an argument is null
     */
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
