package abeona;

import abeona.util.Arguments;

import java.util.Optional;

public class ExplorationTerminationEvent<StateType extends State> extends ExplorationEvent<StateType> {
    private final TerminationType terminationType;
    private final Throwable terminationError;

    public TerminationType getTerminationType() {
        return terminationType;
    }

    public Optional<Throwable> getTerminationError() {
        return Optional.ofNullable(terminationError);
    }

    public ExplorationTerminationEvent(ExplorationQuery<StateType> explorationQuery, TerminationType terminationType) {
        super(explorationQuery);
        this.terminationType = terminationType;
        this.terminationError = null;
    }

    public ExplorationTerminationEvent(ExplorationQuery<StateType> explorationQuery, Throwable error) {
        super(explorationQuery);
        Arguments.requireNonNull(error, "error");
        this.terminationType = TerminationType.RuntimeError;
        this.terminationError = error;
    }
}
