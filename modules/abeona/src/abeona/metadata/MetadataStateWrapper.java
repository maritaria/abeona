package abeona.metadata;

import abeona.util.Arguments;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class that allows you to wrap any state representation into a {@link MetadataState}.
 *
 * @param <StateType> The inner state type to wrap around
 * @apiNote Beware: This hides other feature-interfaces of the wrapped class.
 */
public final class MetadataStateWrapper<StateType> implements MetadataState {
    private final StateType state;
    private final Map<Object, Object> metadata = new HashMap<>();

    /**
     * Wraps an existing state in a {@link MetadataState} compatible wrapper.
     *
     * @param state
     */
    public MetadataStateWrapper(StateType state) {
        Arguments.requireNonNull(state, "state");
        this.state = state;
    }

    /**
     * Get the original state that this wrapper wraps.
     *
     * @return
     */
    public StateType getWrappedState() {
        return state;
    }

    @Override
    public Map<Object, Object> getMetadata() {
        return metadata;
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (state instanceof MetadataStateWrapper) {
            // Silly case: This side nested wrappers
            return state.equals(obj);
        } else if (obj instanceof MetadataStateWrapper) {
            @SuppressWarnings("unchecked") final var other = (MetadataStateWrapper<StateType>) obj;
            final var inner = other.state;
            if (inner instanceof MetadataStateWrapper) {
                // Silly case: Other side nested wrappers
                return equals(inner);
            } else {
                // We have unwrapped to root states
                return state.equals(inner);
            }
        } else {
            // Comparing with non-wrapper
            // Equality test with non-wrapped state is not supported because this could mess up interning in Query.internState
            return false;
        }
    }
}
