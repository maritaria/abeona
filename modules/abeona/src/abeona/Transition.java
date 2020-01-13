package abeona;

import abeona.util.Arguments;

import java.util.Objects;

/**
 * Represents a transition in the statespace.
 * A transition is directional and indicates a link between a source state pointing to a target state.
 * Additionally some userdata can be associated with the transition through {@link #getUserdata()}.
 *
 * @param <StateType>
 */
public final class Transition<StateType> {
    private final StateType source, target;
    private final Object userdata;

    /**
     * Gets the source state of the transition.
     * The {@link Query} is responsible for interning the state instance before the transition is accessible in the transition related event-taps.
     *
     * @return
     */
    public StateType getSourceState() {
        return source;
    }

    /**
     * Gets the target state of the transition.
     * The {@link Query} is responsible for interning the state instance before the transition is accessible in the transition related event-taps.
     *
     * @return
     */
    public StateType getTargetState() {
        return target;
    }

    /**
     * Gets the userdata associated with the transition, can be null.
     * In the case where there are multiple transitions between a pair of states (with the same source-target relationship) then they may be distinguised by the userdata.
     * The userdata is NOT interned, should it reference the source or target state then those references may be not interned and point to different instances.
     *
     * @return
     */
    public Object getUserdata() {
        return userdata;
    }

    /**
     * Constructs a new transition without userdata
     *
     * @param source The source state of the transition
     * @param target The target state of the transition
     * @throws IllegalArgumentException If any of the given states is null
     */
    public Transition(StateType source, StateType target) {
        this(source, target, null);
    }

    /**
     * Constructs a new transition with userdata
     *
     * @param source   The source state of the transition
     * @param target   The target state of the transition
     * @param userdata The userdata to associate with the transition
     * @throws IllegalArgumentException If any of the given states is null
     */
    public Transition(StateType source, StateType target, Object userdata) {
        Arguments.requireNonNull(source, "source");
        Arguments.requireNonNull(target, "target");
        this.source = source;
        this.target = target;
        this.userdata = userdata;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.source, this.target, this.userdata);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Transition) {
            @SuppressWarnings("unchecked") final var other = (Transition<StateType>)obj;
            return Objects.equals(this.source, other.source) &&
                    Objects.equals(this.target, other.target) &&
                    Objects.equals(this.userdata, other.userdata);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return source.toString() + " -> " + target.toString();
    }
}
