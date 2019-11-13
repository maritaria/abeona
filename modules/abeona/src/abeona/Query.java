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
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Holds the description of a search through a state space and is capable of executing such a search.
 * The main components are the frontier, heap and next function.
 * On top of that the query is able to hold a arbitrary metadata for states as well as hold a list of attached behaviours.
 *
 * When building up the query instance the frontier and heap implementation need to be locked in before the behaviours can be attached.
 * The frontier and heap implementations are under your full control but once set they cannot be changed.
 *
 * @param <StateType> The type representing states in the state space.
 */
public final class Query<StateType extends State> {
    private final Frontier<StateType> frontier;
    private final Heap<StateType> heap;
    private final NextFunction<StateType> nextFunction;
    private final WeakHashMap<StateType, StateType> stateIdentities = new WeakHashMap<>();
    private final MetadataStore<StateType> metadata;

    /**
     * Gets the frontier implementation
     * @return The frontier used during exploration, never null
     */
    public Frontier<StateType> getFrontier() {
        return frontier;
    }

    /**
     * Gets the heap implementation
     * @return The heap used during exploration, never null
     */
    public Heap<StateType> getHeap() {
        return heap;
    }

    /**
     * Gives access to attaching arbitrary data to a given state
     * @return The metadata store used during exploration, never null
     */
    public MetadataStore<StateType> getMetadata() {
        return metadata;
    }

    /**
     * Creates a new query using the default metadata store
     * @throws IllegalArgumentException Thrown if a passed argument is null
     * @param frontier The frontier to use during exploration
     * @param heap The heap to use during exploration
     * @param nextFunction The function describing the outgoing transitions for a given state
     */
    public Query(
            Frontier<StateType> frontier,
            Heap<StateType> heap,
            NextFunction<StateType> nextFunction
    ) {
        this(frontier, heap, nextFunction, new LookupMetadataStore<>());
    }

    /**
     * Creates a new query using a specific metadata storage method
     * @throws IllegalArgumentException Thrown if a passed argument is null
     * @param frontier The frontier to use during exploration
     * @param heap The heap to use during exploration
     * @param nextFunction The function describing the outgoing transitions for a given state
     * @param metadata The way to store state metadata
     */
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
        this.nextFunction = nextFunction;
        this.metadata = metadata;
        final var isKnownPredicate = Query.defaultIsKnownPredicate(frontier, heap);
        this.isKnown = new BiFunctionTap<>((query, state) -> isKnownPredicate.test(state));
    }

    /**
     * Event marking the start of exploration of the state space, fired by {@link Query#explore()}
     */
    public final EventTap<ExplorationEvent<StateType>> beforeExploration = new EventTap<>();
    /**
     * Event marking the beginning of picking the next state.
     * This can be used to introduce additional control of exploration termination.
     */
    public final EventTap<ExplorationEvent<StateType>> beforeStatePicked = new EventTap<>();
    /**
     * Event marking the end of the state picking procedure
     */
    public final EventTap<StateEvent<StateType>> afterStatePicked = new EventTap<>();
    /**
     * Event marking the start of evaluation of a state to explore its outgoing transitions
     */
    public final EventTap<StateEvaluationEvent<StateType>> beforeStateEvaluation = new EventTap<>();
    /**
     * Event marking the evaluation of a transition, allowing for filtering transitions out of exploration
     */
    public final EventTap<TransitionEvaluationEvent<StateType>> onTransitionEvaluation = new EventTap<>();
    /**
     * Event marking the discovery of a new state, only fired if a transition from the {@link #onTransitionEvaluation} event is allowed to be saved
     */
    public final EventTap<TransitionEvaluationEvent<StateType>> onStateDiscovery = new EventTap<>();
    /**
     * Event marking the end of a state evaluation, its neighbours have been enumerated and the discovered states have been added to the frontier
     */
    public final EventTap<StateEvaluationEvent<StateType>> afterStateEvaluation = new EventTap<>();
    /**
     * Event marking the end of exploration of the state space, fired by {@link Query#explore()}
     */
    public final EventTap<ExplorationTerminationEvent<StateType>> afterExploration = new EventTap<>();

    /**
     * Point where you can intercept calls to adding states to the frontier
     */
    public final BiFunctionTap<Frontier<StateType>, Stream<StateType>, Boolean> insertIntoFrontier = new BiFunctionTap<>(Frontier::add);
    /**
     * Point where you can intercept the behaviour that produces the next state from the frontier
     */
    public final FunctionTap<Query<StateType>, StateType> pickNextState = new FunctionTap<>(Query::pickNextStateInternal);
    /**
     * Point where you can intercept the determining whether a given state is known or not, the default logic is to test inclusion of the heap and frontier.
     */
    public final BiFunctionTap<Query<StateType>, StateType, Boolean> isKnown;
    /**
     * Point where you can intercept the interning of newly generated states.
     * This logic is needed if the used {@link StateType} does not implement {@link Object#hashCode()} and {@link Object#equals(Object)} properly for use in HashMaps.
     */
    public final FunctionTap<StateType, StateType> internState = new FunctionTap<>(state->stateIdentities.computeIfAbsent(state, Function.identity()));

    /**
     * Explores the state space to its completion.
     * The exploration completes when the frontier is exhausted (empty) or when a {@link TerminateExplorationSignal} is thrown.
     *
     * @return The type of exploration termination that has occurred.
     * @throws RuntimeException Thrown if during exploration any exception except for the {@link TerminateExplorationSignal} is thrown.
     */
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

    /**
     * Perform a single step of exploration:
     * - Pick the next state
     * - Evaluate the neighbours
     * - Add the discoveries to the frontier
     * @return An optional termination indication, empty if the step does not terminate the search, filled with the termination type if the exploration has been terminated.
     * @throws RuntimeException Thrown if during exploration any exception except for the {@link TerminateExplorationSignal} is thrown.
     */
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

    private void evaluateState(StateType state) {
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

    /**
     * Adds an installs a given behaviour for this exploration query.
     * The {@link ExplorationBehaviour#attach(Query)} is called if the behaviour is not yet associated with this query.
     * @param behaviour The behaviour to attach to this query
     */
    public void addBehaviour(ExplorationBehaviour<StateType> behaviour) {
        if (behaviours.add(behaviour)) {
            behaviour.attach(this);
        }
    }

    /**
     * Removes a given behaviour from the query, calling {@link ExplorationBehaviour#detach(Query)} on the behaviour.
     * The detach method is only called if the behaviour is associated with this query through the {@link #addBehaviour(ExplorationBehaviour)} in the first place.
     * @param behaviour The behaviour to detach from this query
     */
    public void removeBehaviour(ExplorationBehaviour<StateType> behaviour) {
        if (behaviours.remove(behaviour)) {
            behaviour.detach(this);
        }
    }

    /**
     * Retrieve the stream of behaviours that are associated with this query.
     * @return The stream containing all behaviours attached to this query through the {@link #addBehaviour(ExplorationBehaviour)} method
     */
    public Stream<? extends ExplorationBehaviour<StateType>> getBehaviours() {
        return behaviours.stream();
    }

    /**
     * Get the behaviours that are associated with this query and are subclasses of a given class {@link T}
     * @param superClass The class to test subclassing for
     * @param <T> The type of behaviour to get instances for
     * @return The stream containing all behaviours attached to this query through the {@link #addBehaviour(ExplorationBehaviour)} method and are subclasses of {@link T}.
     */
    @SuppressWarnings("unchecked")
    public <T extends ExplorationBehaviour<StateType>> Stream<? extends T> getBehaviours(Class<T> superClass) {
        return behaviours.stream()
                .filter(b -> superClass.isAssignableFrom(b.getClass()))
                .map(b -> (T) b);
    }

    private StateType internState(StateType state) {
        return internState.apply(state);
        // return stateIdentities.computeIfAbsent(state, Function.identity());
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
            return new Transition<>(internedSource, internedTarget, transition.getUserdata());
        }
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
