package abeona.metadata;

import abeona.State;
import abeona.util.Arguments;

import java.util.HashMap;
import java.util.Map;

public final class MetadataStateWrapper<StateType extends State> implements MetadataState {
    private final StateType state;
    private final Map<Object, Object> metadata = new HashMap<>();

    public MetadataStateWrapper(StateType state) {
        Arguments.requireNonNull(state, "state");
        this.state = state;
    }

    public StateType getWrappedState() {
        return state;
    }

    @Override
    public Map<Object, Object> getMetadata() {
        return metadata;
    }
}
