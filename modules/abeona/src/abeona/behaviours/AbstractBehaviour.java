package abeona.behaviours;

import abeona.aspects.Tap;
import abeona.ExplorationQuery;
import abeona.State;
import abeona.util.Arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Stream;

// Abstract implementation of a behaviour that contains a mechanism to automatically detach events that get attached.
public abstract class AbstractBehaviour<StateType extends State> implements ExplorationBehaviour<StateType> {
    private final WeakHashMap<ExplorationQuery<StateType>, List<BoundTapHandler<?>>> registrations = new WeakHashMap<>();
    private final List<BoundTapHandler<?>> globalRegistrations = new ArrayList<>();

    protected final <T> void tapQueryBehaviour(ExplorationQuery<StateType> explorationQuery, Tap<T> point, T listener) {
        Arguments.requireNonNull(explorationQuery, "explorationQuery");
        Arguments.requireNonNull(point, "point");
        Arguments.requireNonNull(listener, "listener");
        final var list = registrations.computeIfAbsent(explorationQuery, unused -> new ArrayList<>());
        list.add(new BoundTapHandler<>(point, listener));
    }

    protected final <T> void tapForeignBehaviour(Tap<T> point, T listener) {
        globalRegistrations.add(new BoundTapHandler<>(point, listener));
    }

    protected final Stream<ExplorationQuery<StateType>> registeredQueries() {
        return registrations.keySet().stream();
    }

    protected boolean hasRegisteredTo(ExplorationQuery<StateType> explorationQuery) {
        Arguments.requireNonNull(explorationQuery, "explorationQuery");
        return registrations.containsKey(explorationQuery);
    }

    @Override
    public void detach(ExplorationQuery<StateType> explorationQuery) {
        final var list = registrations.remove(explorationQuery);
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
