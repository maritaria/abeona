package abeona;

import abeona.util.Arguments;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public final class StateEvaluationEvent<StateType extends State> extends ExplorationEvent<StateType> {
    private final StateType state;
    private Stream<TransitionEvaluationEvent<StateType>> transitionEvaluations;

    public StateType getSourceState() {
        return state;
    }

    Stream<TransitionEvaluationEvent<StateType>> getTransitionEvaluations() {
        return transitionEvaluations;
    }

    public StateEvaluationEvent(Query<StateType> query, StateType state, Stream<TransitionEvaluationEvent<StateType>> transitionEvaluations) {
        super(query);
        Arguments.requireNonNull(state, "state");
        Arguments.requireNonNull(transitionEvaluations, "transitionEvaluations");
        this.state = state;
        this.transitionEvaluations = transitionEvaluations;
    }

    public StateEvaluationEvent<StateType> modifyStream(UnaryOperator<Stream<TransitionEvaluationEvent<StateType>>> mutator) {
        final var newEvents = mutator.apply(transitionEvaluations);
        Objects.requireNonNull(newEvents, "mutator returned null stream");
        transitionEvaluations = newEvents;
        return this;
    }

    public StateEvaluationEvent<StateType> peek(Consumer<TransitionEvaluationEvent<StateType>> consumer) {
        Arguments.requireNonNull(consumer, "consumer");
        return modifyStream(stream -> stream.peek(consumer));
    }

    public StateEvaluationEvent<StateType> filter(Predicate<TransitionEvaluationEvent<StateType>> predicate) {
        return modifyStream(stream -> stream.filter(predicate));
    }
}
