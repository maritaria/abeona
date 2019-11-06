package abeona;

import abeona.util.Arguments;

public class StateEvent<StateType extends State> extends ExplorationEvent<StateType> {
    private final StateType state;

    public StateType getState() {
        return state;
    }

    public StateEvent(Query<StateType> query, StateType state) {
        super(query);
        Arguments.requireNonNull(state, "state");
        this.state = state;
    }
}
