package abeona;

import abeona.aspects.EventTap;
import abeona.aspects.BiFunctionTap;
import abeona.aspects.FunctionTap;
import abeona.behaviours.ExplorationBehaviour;
import abeona.frontiers.Frontier;
import abeona.frontiers.ManagedFrontier;
import abeona.heaps.Heap;
import abeona.metadata.LookupMetadataStore;
import abeona.metadata.MetadataStore;
import abeona.util.Arguments;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Query<StateType extends State> {
    private final Frontier<StateType> frontier;
    private final Heap<StateType> heap;
    private final BiFunction<Query<StateType>, StateType, Boolean> isKnownDefault;
    private final NextFunction<StateType> nextFunction;
    private final WeakHashMap<StateType, StateType> stateIdentities = new WeakHashMap<>();
    private final MetadataStore<StateType> metadata;

    public Frontier<StateType> getFrontier() {
        return frontier;
    }

    public Heap<StateType> getHeap() {
        return heap;
    }

    public MetadataStore<StateType> getMetadata() {
        return metadata;
    }

    public Query(
            Frontier<StateType> frontier,
            Heap<StateType> heap,
            NextFunction<StateType> nextFunction
    ) {
        this(frontier, heap, nextFunction, new LookupMetadataStore<>());
    }

    public Query(
            Frontier<StateType> frontier,
            Heap<StateType> heap,
            NextFunction<StateType> nextFunction,
            MetadataStore<StateType> metadata
    ) {
        Arguments.requireNonNull(frontier, "frontier");
        Arguments.requireNonNull(heap, "heap");
        Arguments.requireNonNull(nextFunction, "nextFunction");
        Arguments.requireNonNull(metadata, "metadata");
        this.frontier = frontier;
        this.heap = heap;
        final var q = Query.defaultIsKnownPredicate(frontier, heap);
        this.isKnownDefault = (query, state) -> q.test(state);
        this.isKnown = new BiFunctionTap<>(this.isKnownDefault);
        this.nextFunction = nextFunction;
        this.metadata = metadata;
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
    public final FunctionTap<Query<StateType>, StateType> pickNextState = new FunctionTap<>(Query::pickNextStateInternal);
    public final BiFunctionTap<Query<StateType>, StateType, Boolean> isKnown;

    public TerminationType explore() {
        try {
            beforeExploration.accept(new ExplorationEvent<>(this));
            while (frontier.hasNext()) {
                explorationStep();
            }
        } catch (TerminateExplorationSignal signal) {
            afterExploration.accept(new ExplorationTerminationEvent<>(this, TerminationType.ManualTermination));
            return TerminationType.ManualTermination;
        } catch (Throwable error) {
            afterExploration.accept(new ExplorationTerminationEvent<>(this, error));
            throw new RuntimeException("An error occurred during exploration", error);
        }
        afterExploration.accept(new ExplorationTerminationEvent<>(this, TerminationType.FrontierExhaustion));
        return TerminationType.FrontierExhaustion;
    }

    public Optional<TerminationType> exploreNext() {
        if (!frontier.hasNext()) {
            return Optional.of(TerminationType.FrontierExhaustion);
        } else {
            try {
                explorationStep();
                return Optional.empty();
            } catch (TerminateExplorationSignal signal) {
                afterExploration.accept(new ExplorationTerminationEvent<>(this, TerminationType.ManualTermination));
                return Optional.of(TerminationType.ManualTermination);
            } catch (Throwable error) {
                afterExploration.accept(new ExplorationTerminationEvent<>(this, error));
                throw new RuntimeException("An error occurred during exploration", error);
            }
        }
    }

    private void explorationStep() {
        final StateType next = pickNextState();
        evaluateState(next);
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
        final var evaluationEvents = nextFunction.apply(state)
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
        event.filterTargetState(state -> !this.isKnown.apply(this, state));
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

    private static <StateType extends State> Predicate<StateType> defaultIsKnownPredicate(Frontier<StateType> frontier, Heap<StateType> heap) {
        final Predicate<StateType> inHeap = heap::contains;
        if (frontier instanceof ManagedFrontier) {
            final ManagedFrontier<StateType> managedFrontier = (ManagedFrontier<StateType>) frontier;
            final Predicate<StateType> inFrontier = managedFrontier::contains;
            return inFrontier.or(inHeap);
        } else {
            return inHeap;
        }
    }
}
