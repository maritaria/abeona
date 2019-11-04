package abeona.metadata;

import abeona.State;

import java.util.Map;

public interface MetadataState extends State {
    Map<Object, Object> getMetadata();
}
