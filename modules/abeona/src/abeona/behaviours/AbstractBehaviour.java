package abeona.behaviours;

import abeona.aspects.Tap;
import abeona.Query;
import abeona.State;
import abeona.util.Arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Stream;

// Abstract implementation of a behaviour that contains a mechanism to automatically detach events that get attached.
public abstract class AbstractBehaviour<StateType extends State> implements ExplorationBehaviour<StateType> {
    private final WeakHashMap<Query<StateType>, List<BoundTapHandler<?>>> registrations = new WeakHashMap<>();
    private final List<BoundTapHandler<?>> globalRegistrations = new ArrayList<>();

    protected final <T> void tapQueryBehaviour(Query<StateType> query, Tap<T> point, T listener) {
        Arguments.requireNonNull(query, "query");
        Arguments.requireNonNull(point, "point");
        Arguments.requireNonNull(listener, "listener");
        final var list = registrations.computeIfAbsent(query, unused -> new ArrayList<>());
        list.add(new BoundTapHandler<>(point, listener));
    }

    protected final <T> void tapForeignBehaviour(Tap<T> point, T listener) {
        globalRegistrations.add(new BoundTapHandler<>(point, listener));
    }

    protected final Stream<Query<StateType>> registeredQueries() {
        return registrations.keySet().stream();
    }

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
