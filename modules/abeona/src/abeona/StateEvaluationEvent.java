package abeona;

import abeona.util.Arguments;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Eventdata for the {@link Query#beforeStateEvaluation} and {@link Query#afterStateEvaluation} event taps.
 * You can access the state being evaluated as well as mutate the stream of outgoing transitions that are being evaluated.
 * @param <StateType>
 */
public final class StateEvaluationEvent<StateType extends State> extends ExplorationEvent<StateType> {
    private final StateType state;
    private Stream<TransitionEvaluationEvent<StateType>> transitionEvaluations;

    /**
     * Gets the state being evaluated
     * @return
     */
    public StateType getSourceState() {
        return state;
    }

    Stream<TransitionEvaluationEvent<StateType>> getTransitionEvaluations() {
        return transitionEvaluations;
    }

    StateEvaluationEvent(Query<StateType> query, StateType state, Stream<TransitionEvaluationEvent<StateType>> transitionEvaluations) {
        super(query);
        Arguments.requireNonNull(state, "state");
        Arguments.requireNonNull(transitionEvaluations, "transitionEvaluations");
        this.state = state;
        this.transitionEvaluations = transitionEvaluations;
    }

    /**
     * Allows the stream of outgoing transitions to be modified arbitrarily.
     * The mutator receives the current stream and has to return a new stream instance.
     * The new stream returned by the mutator replaces the existing stream in the event.
     *
     * You can use the {@link Stream#peek(Consumer)} function on the existing stream to piggy-back enumeration of the stream when it is evaluated.
     * The moment of evaluation of the stream is determinted by the {@link Query#getFrontier()} instance, unless the stream is modified by an operation which evaluates it and creates a new stream based on the collected transitions.
     *
     * @param mutator The operator that changes the current stream into a modified one.
     * @throws NullPointerException Thrown if the given mutator returns null
     * @return Returns the event instance to allow for chaining.
     */
    public StateEvaluationEvent<StateType> modifyStream(UnaryOperator<Stream<TransitionEvaluationEvent<StateType>>> mutator) {
        Arguments.requireNonNull(mutator, "mutator");
        final var newEvents = mutator.apply(transitionEvaluations);
        Objects.requireNonNull(newEvents, "mutator returned null stream");
        transitionEvaluations = newEvents;
        return this;
    }
}
