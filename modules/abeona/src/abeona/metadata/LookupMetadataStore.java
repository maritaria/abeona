package abeona.metadata;

import abeona.State;
import abeona.util.Arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public final class LookupMetadataStore<StateType extends State> implements MetadataStore<StateType> {
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
