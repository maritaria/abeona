package abeona.behaviours;

import abeona.Query;
import abeona.State;
import abeona.StateEvaluationEvent;

import java.util.Map;
import java.util.OptionalInt;
import java.util.WeakHashMap;

public final class IterationCounter<StateType extends State> extends AbstractBehaviour<StateType> {
    private Map<Query<StateType>, Integer> counters = new WeakHashMap<>(1);

    public OptionalInt getCounter(Query<StateType> query) {
        final var count = counters.get(query);
        return count == null ? OptionalInt.empty() : OptionalInt.of(count);
    }

    public void setCounter(Query<StateType> query, int value) {
        counters.put(query, value);
    }

    @Override
    public void attach(Query<StateType> query) {
        tapQueryBehaviour(query, query.afterStateEvaluation, this::afterStateEvaluation);
    }

    private void afterStateEvaluation(StateEvaluationEvent<StateType> event) {
        final var query = event.getQuery();
        setCounter(query, getCounter(query).orElse(0) + 1);
    }
}
