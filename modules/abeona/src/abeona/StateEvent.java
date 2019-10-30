package abeona;

import abeona.util.Arguments;

public class StateEvent<StateType extends State> extends ExplorationEvent<StateType> {
    private final StateType state;

    public StateType getState() {
        return state;
    }

    public StateEvent(ExplorationQuery<StateType> explorationQuery, StateType state) {
        super(explorationQuery);
        Arguments.requireNonNull(state, "state");
        this.state = state;
    }
}
