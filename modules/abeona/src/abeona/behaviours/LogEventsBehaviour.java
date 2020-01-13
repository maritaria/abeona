package abeona.behaviours;

import abeona.*;
import abeona.frontiers.Frontier;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Utility behaviour that prints to console when event taps fire in a query instance.
 * @param <StateType>
 */
public final class LogEventsBehaviour<StateType> extends AbstractBehaviour<StateType> {
    @Override
    public void attach(Query<StateType> query) {
        // TODO: Fix the need of exact query state typing (optional here but difficult to remove)
        query.beforeExploration.tap(this::beforeExploration);
        query.beforeStatePicked.tap(this::beforeStatePicked);
        query.pickNextState.tap(this::pickNextState);
        query.afterStatePicked.tap(this::afterStatePicked);
        query.beforeStateEvaluation.tap(this::beforeStateEvaluation);
        query.onTransitionEvaluation.tap(this::onTransitionEvaluation);
        query.onStateDiscovery.tap(this::onStateDiscovery);
        query.insertIntoFrontier.tap(this::insertIntoFrontier);
        query.afterStateEvaluation.tap(this::afterStateEvaluation);
        query.afterExploration.tap(this::afterExploration);
    }

    private void beforeExploration(ExplorationEvent<StateType> event) {
        System.out.println("beforeExploration");
    }

    private void beforeStatePicked(ExplorationEvent<StateType> event) {
        System.out.println("beforeStatePicked");
    }

    private StateType pickNextState(Query<StateType> query, Function<Query<StateType>, StateType> next) {
        System.out.println("pickNextState");
        return next.apply(query);
    }

    private void afterStatePicked(StateEvent<StateType> event) {
        System.out.println("afterStatePicked: " + event.getState());
    }

    private void beforeStateEvaluation(StateEvaluationEvent<StateType> event) {
        System.out.println("beforeStateEvaluation: " + event.getSourceState());
    }

    private void onTransitionEvaluation(TransitionEvaluationEvent<StateType> event) {
        System.out.println("onTransitionEvaluation: " + event.getTransition());
    }

    private void onStateDiscovery(TransitionEvaluationEvent<StateType> event) {
        System.out.println("onStateDiscovery: " + event.getTransition().getTargetState());
    }

    private boolean insertIntoFrontier(Frontier<StateType> frontier, Stream<StateType> states, BiFunction<Frontier<StateType>, Stream<StateType>, Boolean> next) {
        return next.apply(frontier, states.peek(state -> System.out.println("insertInfoFrontier: " + state)));
    }

    private void afterStateEvaluation(StateEvaluationEvent<StateType> event) {
        System.out.println("afterStateEvaluation: " + event.getSourceState());
    }

    private void afterExploration(ExplorationTerminationEvent<StateType> event) {
        System.out.println("afterExploration");
    }

}
