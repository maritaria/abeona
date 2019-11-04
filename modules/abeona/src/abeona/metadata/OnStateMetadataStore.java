package abeona.metadata;

import java.util.Optional;

public final class OnStateMetadataStore<StateType extends MetadataState> implements MetadataStore<StateType> {
    @Override
    public void set(StateType state, Object key, Object value) {
        state.getMetadata().put(key, value);
    }

    @Override
    public Optional<Object> get(StateType state, Object key) {
        return Optional.ofNullable(state.getMetadata().get(key));
    }
}
