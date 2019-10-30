package abeona;

import abeona.util.Arguments;

public class ExplorationEvent<StateType extends State> {
    private final ExplorationQuery<StateType> explorationQuery;

    public ExplorationQuery<StateType> getQuery() {
        return explorationQuery;
    }

    public ExplorationEvent(ExplorationQuery<StateType> explorationQuery) {
        Arguments.requireNonNull(explorationQuery, "explorationQuery");
        this.explorationQuery = explorationQuery;
    }

    public void abortExploration() {
        throw new TerminateExplorationSignal();
    }
}
