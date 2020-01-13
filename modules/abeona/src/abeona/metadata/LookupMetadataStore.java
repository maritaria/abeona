package abeona.metadata;

import abeona.util.Arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Implements a metadata store that uses {@link WeakHashMap} to associate metadata to states.
 * @apiNote The first call to {@link #set} should be done with an interned state, otherwise the instance may drop off and the metadata will get lost dispite another one still existing.
 * @param <StateType>
 */
public final class LookupMetadataStore<StateType> implements MetadataStore<StateType> {
    private final Map<StateType, Map<Object, Object>> metadata = new WeakHashMap<>();

    @Override
    public void set(StateType state, Object key, Object value) {
        Arguments.requireNonNull(state, "state");
        Arguments.requireNonNull(key, "key");
        final var meta = metadata.computeIfAbsent(state, unused -> new HashMap<>());
        if (value != null) {
            meta.put(key, value);
        } else {
            meta.remove(key);
        }
    }

    @Override
    public Optional<Object> get(StateType state, Object key) {
        Arguments.requireNonNull(state, "state");
        Arguments.requireNonNull(key, "key");
        return Optional.ofNullable(metadata.get(state))
                .flatMap(meta -> Optional.ofNullable(meta.get(key)));
    }
}
