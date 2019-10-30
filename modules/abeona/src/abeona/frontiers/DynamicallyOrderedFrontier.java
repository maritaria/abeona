package abeona.frontiers;

import abeona.State;

import java.util.function.Consumer;

public interface DynamicallyOrderedFrontier<StateType extends State> extends OrderedFrontier<StateType> {
    /**
     * Updates the position of a state in the frontier after a mutation is applied to the state that mutates a property the sort order of the frontier is based on.
     * The mutator is guaranteed to be invoked regardless whether the state is present in the frontier or not.
     *
     * The mutator will be invoked before this method returns.
     * If the mutator throws an exception then the sort update of the state will proceed but the exception will be re-thrown before the method completes.
     * This ensures the state of the frontier remains consistent while also allowing the error to be properly handled.
     *
     * @param state The state that you want to mutate
     * @param mutator The function that mutates the state, is guaranteed to be called
     */
    void mutateOrderedProperty(StateType state, Consumer<StateType> mutator);
}
