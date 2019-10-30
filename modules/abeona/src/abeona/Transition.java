package abeona;

import abeona.util.Arguments;

public final class Transition<StateType extends State> {
    private final StateType source, target;

    public StateType getSourceState() {
        return source;
    }

    public StateType getTargetState() {
        return target;
    }

    public Transition(StateType source, StateType target) {
        Arguments.requireNonNull(source, "source");
        Arguments.requireNonNull(target, "target");
        this.source = source;
        this.target = target;
    }

    // TODO: Store userdata
}
