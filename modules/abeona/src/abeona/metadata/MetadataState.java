package abeona.metadata;

import abeona.State;

import java.util.Map;

/**
 * Interface for state types that identify them to support on-state storage of metadata.
 * Useful in combination with interning through {@link abeona.Query#internState(State)}.
 * You can use {@link MetadataStateWrapper} to wrap any existing state representation.
 */
public interface MetadataState extends State {
    Map<Object, Object> getMetadata();
}
