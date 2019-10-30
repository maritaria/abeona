package abeona;

public interface State {
    @Override
    boolean equals(Object obj);

    boolean equivalent(State other);
}
