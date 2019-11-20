package abeona;

import abeona.util.Arguments;

import java.util.Optional;

/**
 * Eventdata for signalling the termination of the exploration loop.
 * It includes the type of termination ({@link TerminationType}) under which the exploration was stopped.
 * @param <StateType> The type of state in the statespace
 */
public class ExplorationTerminationEvent<StateType extends State> extends ExplorationEvent<StateType> {
    private final TerminationType terminationType;
    private final Throwable terminationError;

    /**
     * Gets the type of cause for the termination
     * @return
     */
    public TerminationType getTerminationType() {
        return terminationType;
    }

    /**
     * Returns an optional reference to the error that ended exploration.
     * The optional guaranteed to hold the {@link Throwable} that ended exploration if and only if the type of termination is {@link TerminationType#RuntimeError}.
     * The {@link TerminateExplorationSignal} is the only type of exception that does not end up in the optional.
     * @return
     */
    public Optional<Throwable> getTerminationError() {
        return Optional.ofNullable(terminationError);
    }

    ExplorationTerminationEvent(Query<StateType> query, TerminationType terminationType) {
        super(query);
        this.terminationType = terminationType;
        this.terminationError = null;
    }

    ExplorationTerminationEvent(Query<StateType> query, Throwable error) {
        super(query);
        Arguments.requireNonNull(error, "error");
        this.terminationType = TerminationType.RuntimeError;
        this.terminationError = error;
    }
}
