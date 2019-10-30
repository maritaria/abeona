package abeona;

import abeona.aspects.EventTap;
import abeona.aspects.BiFunctionTap;
import abeona.aspects.FunctionTap;
import abeona.behaviours.ExplorationBehaviour;
import abeona.frontiers.Frontier;
import abeona.heaps.Heap;
import abeona.util.Arguments;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ExplorationQuery<StateType extends State> {
    private final Frontier<StateType> frontier;
    private final Heap<StateType> heap;
    private final Function<StateType, Stream<Transition<StateType>>> outgoingTransitionGenerator;
    private final WeakHashMap<StateType, StateType> stateIdentities = new WeakHashMap<>();

    public Frontier<StateType> getFrontier() {
        return frontier;
    }

    public Heap<StateType> getHeap() {
        return heap;
    }

    public ExplorationQuery(
            Frontier<StateType> frontier,
            Heap<StateType> heap,
            Function<StateType, Stream<Transition<StateType>>> outgoingTransitionGenerator
    ) {
        this.frontier = frontier;
        this.heap = heap;
        this.outgoingTransitionGenerator = outgoingTransitionGenerator;
    }

    public final EventTap<ExplorationEvent<StateType>> beforeExploration = new EventTap<>();
    public final EventTap<ExplorationEvent<StateType>> beforeStatePicked = new EventTap<>();
    public final EventTap<StateEvent<StateType>> afterStatePicked = new EventTap<>();
    public final EventTap<StateEvaluationEvent<StateType>> beforeStateEvaluation = new EventTap<>();
    public final EventTap<TransitionEvaluationEvent<StateType>> onTransitionEvaluation = new EventTap<>();
    public final EventTap<TransitionEvaluationEvent<StateType>> onStateDiscovery = new EventTap<>();
    public final EventTap<StateEvaluationEvent<StateType>> afterStateEvaluation = new EventTap<>();
    public final EventTap<ExplorationTerminationEvent<StateType>> afterExploration = new EventTap<>();

    public final BiFunctionTap<Frontier<StateType>, Stream<StateType>, Boolean> insertIntoFrontier = new BiFunctionTap<>(Frontier::add);
    public final FunctionTap<ExplorationQuery<StateType>, StateType> pickNextState = new FunctionTap<>(ExplorationQuery::pickNextStateInternal);

    public TerminationType explore() {
        try {
            beforeExploration.accept(new ExplorationEvent<>(this));
            while (frontier.hasNext()) {
                final StateType next = pickNextState();
                this.evaluateState(next);
            }
        } catch (TerminateExplorationSignal signal) {
            afterExploration.accept(new ExplorationTerminationEvent<>(this, TerminationType.ManualTermination));
            return TerminationType.FrontierExhaustion;
        } catch (Throwable error) {
            // TODO: Verify no fall-through for the catches
            afterExploration.accept(new ExplorationTerminationEvent<>(this, error));
            throw new RuntimeException("An error occurred during exploration", error);
        }
        afterExploration.accept(new ExplorationTerminationEvent<>(this, TerminationType.FrontierExhaustion));
        return TerminationType.FrontierExhaustion;
    }

    private StateType pickNextState() {
        beforeStatePicked.accept(new ExplorationEvent<>(this));
        final var next = pickNextState.apply(this);
        heap.add(next);
        afterStatePicked.accept(new StateEvent<>(this, next));
        return next;
    }

    private StateType pickNextStateInternal() {
        return frontier.next();
    }

    public void evaluateState(StateType state) {
        Arguments.requireNonNull(state, "state");
        final var evaluationEvents = outgoingTransitionGenerator.apply(state)
                .map(this::internTransition)
                .map(this::createTransitionEvaluationEvent)
                .peek(onTransitionEvaluation)
                .filter(TransitionEvaluationEvent::getSaveTargetState)
                .peek(onStateDiscovery);
        final var evaluationEvent = new StateEvaluationEvent<>(this, state, evaluationEvents);
        beforeStateEvaluation.accept(evaluationEvent);
        final var discoveredStates = evaluationEvent.getTransitionEvaluations()
                .map(TransitionEvaluationEvent::getTransition)
                .map(Transition::getTargetState);
        insertIntoFrontier.apply(frontier, discoveredStates);
        afterStateEvaluation.accept(evaluationEvent);
    }

    private TransitionEvaluationEvent<StateType> createTransitionEvaluationEvent(Transition<StateType> transition) {
        Arguments.requireNonNull(transition, "transition");
        final var event = new TransitionEvaluationEvent<>(this, transition);
        event.filterTargetState(heap::contains);
        return event;
    }

    private final Set<ExplorationBehaviour<StateType>> behaviours = new HashSet<>();

    public void addBehaviour(ExplorationBehaviour<StateType> behaviour) {
        if (behaviours.add(behaviour)) {
            behaviour.attach(this);
        }
    }

    public void removeBehaviour(ExplorationBehaviour<StateType> behaviour) {
        if (behaviours.remove(behaviour)) {
            behaviour.detach(this);
        }
    }

    public Stream<? extends ExplorationBehaviour<StateType>> getBehaviours() {
        return behaviours.stream();
    }

    @SuppressWarnings("unchecked")
    public <T extends ExplorationBehaviour<StateType>> Stream<? extends T> getBehaviours(Class<T> superClass) {
        return behaviours.stream()
                .filter(b -> superClass.isAssignableFrom(b.getClass()))
                .map(b -> (T) b);
    }

    public StateType internState(StateType state) {
        Arguments.requireNonNull(state, "state");
        return stateIdentities.computeIfAbsent(state, Function.identity());
        // TODO: Documentation
        // TODO: Unit tests
    }

    private Transition<StateType> internTransition(Transition<StateType> transition) {
        Arguments.requireNonNull(transition, "transition");
        final var originalTarget = transition.getTargetState();
        final var internedTarget = internState(originalTarget);
        final var originalSource = transition.getSourceState();
        final var internedSource = internState(originalSource);
        if (originalTarget == internedTarget && originalSource == internedSource) {
            return transition;
        } else {
            return new Transition<>(internedSource, internedTarget);
        }
        // TODO: Documentation
        // TODO: Unit tests
    }
}
