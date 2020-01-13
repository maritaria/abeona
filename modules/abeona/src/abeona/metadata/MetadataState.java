package abeona.metadata;

import java.util.Map;

/**
 * Interface for state types that identify them to support on-state storage of metadata.
 * Useful in combination with interning through {@link abeona.Query#internState)}.
 * You can use {@link MetadataStateWrapper} to wrap any existing state representation.
 */
public interface MetadataState {
    Map<Object, Object> getMetadata();
}
