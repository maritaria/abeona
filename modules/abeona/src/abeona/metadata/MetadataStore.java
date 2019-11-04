package abeona.metadata;

import abeona.State;

import java.util.Optional;
import java.util.function.Function;

public interface MetadataStore<StateType extends State> {
    void set(StateType state, Object key, Object value);

    Optional<Object> get(StateType state, Object key);

    default Object getOrCompute(StateType state, Object key, Function<StateType, Object> valueGenerator) {
        return get(state, key).orElseGet(() -> {
            final var value = valueGenerator.apply(state);
            set(state, key, value);
            return value;
        });
    }
}
