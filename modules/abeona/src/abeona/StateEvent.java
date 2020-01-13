package abeona;

import abeona.util.Arguments;

/**
 * Base eventdata class for {@link ExplorationEvent} based events that are related to a particular state as well
 * @param <StateType>
 */
public class StateEvent<StateType> extends ExplorationEvent<StateType> {
    private final StateType state;

    /**
     * Gets the state to which the event is related
     * @return
     */
    public StateType getState() {
        return state;
    }

    /**
     * Creates a new StateEvent instance
     * @param query The query to which the event is related
     * @param state The state to which the event is related
     * @throws IllegalArgumentException if any passed argument is null
     */
    public StateEvent(Query<StateType> query, StateType state) {
        super(query);
        Arguments.requireNonNull(state, "state");
        this.state = state;
    }
}
