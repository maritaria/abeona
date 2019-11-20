package abeona;

import abeona.util.Arguments;

/**
 * A eventdata class that serves as a base for all query/exploration related events.
 * Events fired from/by a Query instance usually are subclassed from this class
 * @param <StateType> The type of state in the statespace
 */
public class ExplorationEvent<StateType extends State> {
    private final Query<StateType> query;

    /**
     * Get the query to which the event is related
     * @return The query
     */
    public Query<StateType> getQuery() {
        return query;
    }

    /**
     * Creates a new ExplorationEvent instance
     * @param query The query to which the event is related
     * @throws IllegalArgumentException if any passed argument is null
     */
    public ExplorationEvent(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        this.query = query;
    }

    /**
     * Notify the Query to stop the exploration loop.
     * If the event is fired by the query and the {@link Query#explore()} or {@link Query#exploreNext()} is being executed then that method terminates with the {@link TerminationType#ManualTermination} signal.
     * @throws TerminateExplorationSignal
     */
    public void abortExploration() {
        throw new TerminateExplorationSignal();
    }
}
