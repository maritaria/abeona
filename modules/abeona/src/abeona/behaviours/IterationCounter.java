package abeona.behaviours;

import abeona.Query;
import abeona.StateEvaluationEvent;
import abeona.util.Arguments;

import java.util.Map;
import java.util.OptionalInt;
import java.util.WeakHashMap;

/**
 * This behaviour keeps count of how many states get evaluated by a query.
 * @param <StateType>
 */
public final class IterationCounter<StateType> extends AbstractBehaviour<StateType> {
    private Map<Query<StateType>, Integer> counters = new WeakHashMap<>(1);

    /**
     * Get the number of (finished) state evaluations performed by the query
     * @param query
     * @return The number of state evaluations that the query performed since this behaviour has been attached, empty if this behaviour is not attached to the query
     * @throws IllegalArgumentException Thrown if the query is null
     */
    public OptionalInt getCounter(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        final var count = counters.get(query);
        return count == null ? OptionalInt.empty() : OptionalInt.of(count);
    }

    /**
     * Set the counter of state evaluations to a specific value for a given query
     * @param query The query to change the counter for
     * @param value The value to set the counter to
     * @throws IllegalArgumentException Thrown if the query is null
     */
    public void setCounter(Query<StateType> query, int value) {
        Arguments.requireNonNull(query, "query");
        counters.put(query, value);
    }

    /**
     * Installs the behaviour and sets the counter for the query to 0
     * @param query The instance to install logic into
     * @throws IllegalArgumentException Thrown if the query is null
     */
    @Override
    public void attach(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        tapQueryBehaviour(query, query.afterStateEvaluation, this::afterStateEvaluation);
        setCounter(query, 0);
    }

    @Override
    public void detach(Query<StateType> query) {
        super.detach(query);
        counters.remove(query);
    }

    private void afterStateEvaluation(StateEvaluationEvent<StateType> event) {
        final var query = event.getQuery();
        setCounter(query, getCounter(query).orElseThrow() + 1);
    }
}
