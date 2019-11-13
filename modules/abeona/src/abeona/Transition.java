package abeona;

import abeona.util.Arguments;

/**
 * Represents the 
 * @param <StateType>
 */
public final class Transition<StateType extends State> {
    private final StateType source, target;
    private final Object userdata;

    public StateType getSourceState() {
        return source;
    }

    public StateType getTargetState() {
        return target;
    }

    public Transition(StateType source, StateType target) {
        this(source, target, null);
    }

    public Transition(StateType source, StateType target, Object userdata) {
        Arguments.requireNonNull(source, "source");
        Arguments.requireNonNull(target, "target");
        this.source = source;
        this.target = target;
        this.userdata = userdata;
    }

    // TODO: Store userdata

    @Override
    public String toString() {
        return source.toString() + " -> " + target.toString();
    }

    public Object getUserdata() {
        return userdata;
    }
}
