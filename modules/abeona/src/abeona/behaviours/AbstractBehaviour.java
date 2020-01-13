package abeona.behaviours;

import abeona.aspects.Tap;
import abeona.Query;
import abeona.util.Arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Stream;

/**
 * Abstract implementation of a behaviour that contains a mechanism to automatically detach events that get attached.
 * @param <StateType>
 */
public abstract class AbstractBehaviour<StateType> implements ExplorationBehaviour<StateType> {
    private final WeakHashMap<Query<StateType>, List<BoundTapHandler<?>>> registrations = new WeakHashMap<>();
    private final List<BoundTapHandler<?>> globalRegistrations = new ArrayList<>();

    /**
     * Tap a behaviour of a query, if {@link ExplorationBehaviour#detach(Query)} is called then this tap is automatically undone.
     * The tap is also undone when {@link #detachAll()} is used.
     * @param query The query to attach a tap to
     * @param point The Tappable behaviour to tap into
     * @param listener The handler to install into the tap
     * @param <T> The type of tap handler
     * @throws IllegalArgumentException Thrown if any argument is null
     */
    protected final <T> void tapQueryBehaviour(Query<StateType> query, Tap<T> point, T listener) {
        Arguments.requireNonNull(query, "query");
        Arguments.requireNonNull(point, "point");
        Arguments.requireNonNull(listener, "listener");
        final var list = registrations.computeIfAbsent(query, unused -> new ArrayList<>());
        list.add(new BoundTapHandler<>(point, listener));
    }

    /**
     * Tap a non-query behaviour, this tap is not undone when a query is detached through {@link ExplorationBehaviour#detach(Query)}.
     * However, the tap is undone when the {@link #detachAll()} is invoked
     * @param point The tappable to tap into
     * @param listener The listener to install into the tap
     * @param <T> The type of handler the tap uses
     */
    protected final <T> void tapForeignBehaviour(Tap<T> point, T listener) {
        globalRegistrations.add(new BoundTapHandler<>(point, listener));
    }

    /**
     * Stream of the querries the behaviour is attached to
     * @return
     */
    protected final Stream<Query<StateType>> registeredQueries() {
        return registrations.keySet().stream();
    }

    /**
     * Indicates whether this behaviour has been attached to the given query.
     * @param query The query to test attachment for
     * @return True if any tap handlers have been registered through {@link #tapQueryBehaviour(Query, Tap, Object)} with the given query instance, false otherwise.
     */
    protected boolean hasRegisteredTo(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        return registrations.containsKey(query);
    }

    @Override
    public void detach(Query<StateType> query) {
        final var list = registrations.remove(query);
        if (list != null) {
            for (BoundTapHandler<?> behaviour : list) {
                behaviour.detach();
            }
        }
    }

    /**
     * Performs {@link #detach(Query)} on all attached querries as well as unregister all non-query tap handlers registered through {@link #tapForeignBehaviour(Tap, Object)}
     */
    public void detachAll() {
        while (!registrations.isEmpty()) {
            detach(registrations.keySet().iterator().next());
        }
        for (BoundTapHandler<?> behaviour : globalRegistrations) {
            behaviour.detach();
        }
        globalRegistrations.clear();
    }

    private static class BoundTapHandler<T> {
        final Tap<T> point;
        final T handler;

        BoundTapHandler(Tap<T> tap, T handler) {
            Arguments.requireNonNull(tap, "behaviour");
            Arguments.requireNonNull(handler, "handler");
            this.point = tap;
            this.handler = handler;
            tap.tap(handler);
        }

        void detach() {
            point.unTap(handler);
        }
    }
}
