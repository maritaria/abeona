package abeona;

import abeona.util.Arguments;

public class ExplorationEvent<StateType extends State> {
    private final Query<StateType> query;

    public Query<StateType> getQuery() {
        return query;
    }

    public ExplorationEvent(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        this.query = query;
    }

    public void abortExploration() {
        throw new TerminateExplorationSignal();
    }
}
