package abeona.metadata;

import java.util.Optional;
import java.util.function.Function;

/**
 * Interface for a mechanism that stores arbitrary (meta)data on states.
 * @param <StateType>
 */
public interface MetadataStore<StateType> {
    /**
     * Sets a metadata value on a state
     * @param state The state to set the value on
     * @param key The key under which the value will be stored
     * @param value The value to store under the given key on the given state
     */
    void set(StateType state, Object key, Object value);

    /**
     * Retrieves (if present) a value on a given state under a given metadata-key
     * @param state The state to get metadata for
     * @param key The key under which the value is stored
     * @return An optional holding the metadata value associated with the state, empty if the state does not have data associated for the given key
     */
    Optional<Object> get(StateType state, Object key);

    /**
     * Helper function which gets the value of a metadata key and generates a value if none is present.
     * @param state The state to get metadata for
     * @param key The key under which the value might exist
     * @param valueGenerator A generator that provides the fallback value if the key has no metadata associated yet.
     * @return The existing value or the value generated by the generator.
     */
    default Object getOrCompute(StateType state, Object key, Function<StateType, Object> valueGenerator) {
        return get(state, key).orElseGet(() -> {
            final var value = valueGenerator.apply(state);
            set(state, key, value);
            return value;
        });
    }
}
