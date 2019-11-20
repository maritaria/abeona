package abeona.behaviours;

import abeona.Query;
import abeona.State;

/**
 * Defines a piece of exploration behaviour that can be attached to a {@link Query} instance.
 * @param <StateType>
 */
public interface ExplorationBehaviour<StateType extends State> {
    /**
     * Attaches the behaviour logic to a given query
     * @param query The instance to install logic into
     */
    void attach(Query<StateType> query);

    /**
     * Removes a behaviour from an existing query
     * @param query The query to remove the logic from
     */
    void detach(Query<StateType> query);
}
