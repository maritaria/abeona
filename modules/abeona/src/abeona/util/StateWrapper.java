package abeona.util;

import abeona.State;

public final class StateWrapper<T> implements State {
    private final T value;

    public StateWrapper(T value) {
        Arguments.requireNonNull(value, "value");
        this.value = value;
    }

    public T unwrap() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StateWrapper) {
            final var other = (StateWrapper) obj;
            return value.equals(other.value);
        } else {
            return value.equals(obj);
        }
    }

    @Override
    public String toString() {
        return "WrapperState (" + value.toString() + ")";
    }
}
