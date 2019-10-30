package abeona.aspects;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class AbstractTapTest {
    static class Impl<T> extends AbstractTap<T> {
        LinkedList<T> getHandlers() {
            return handlers;
        }

        Impl() {
            super();
        }
    }

    @Test
    void constructor() {
        final var instance = assertDoesNotThrow(() -> new Impl<>());
        assertNotNull(instance.getHandlers(), "Handlers should not be null after default constructor");
        assertEquals(0, instance.getHandlers().size(), "Handlers should be empty after default constructor");
    }

    @Test
    void tap() {
        final var impl = new Impl<Consumer<Object>>();
        final Consumer<Object> handler1 = Object::notify;
        final Consumer<Object> handler2 = Object::notifyAll;
        assertThrows(IllegalArgumentException.class, () -> impl.tap(null));
        final var registered = impl.tap(handler1);
        assertEquals(handler1, registered, "The returned function is not equal to the given handler");
        assertEquals(1, impl.getHandlers().size(), "Handlers should contain the first registered callback");
        assertEquals(registered, impl.getHandlers().get(0), "The returned handler is not the same as the stored handler");
        impl.tap(handler2);
        assertEquals(2, impl.getHandlers().size(), "Second handler failed to register");
        assertEquals(handler2, impl.getHandlers().get(0), "The second handler was not added in the front");
        assertEquals(handler1, impl.getHandlers().get(1), "The first handler was not pushed to the back");
        impl.tap(handler1);
        assertEquals(3, impl.getHandlers().size(), "Duplicate handler registration failed");
        assertEquals(handler1, impl.getHandlers().get(0), "Duplicate handler not added to the front");
        assertEquals(handler2, impl.getHandlers().get(1));
        assertEquals(handler1, impl.getHandlers().get(2));
    }

    @Test
    void unTap() {
        final var impl = new Impl<Consumer<Object>>();
        final Consumer<Object> handler1 = Object::notify;
        final Consumer<Object> handler2 = Object::notifyAll;
        impl.tap(handler1);
        assertThrows(IllegalArgumentException.class, () -> impl.unTap(null));
        assertDoesNotThrow(() -> impl.unTap(handler2), "Attempt to untap unknown handler should have no effect");
        assertEquals(1, impl.getHandlers().size(), "Registered handler should still be registered after illegal and unknown untaps");
        impl.tap(handler2);
        assertEquals(2, impl.getHandlers().size(), "Registering previously (invalidly) unregistered handler should not prevent future tapping");
        impl.unTap(handler1);
        assertEquals(1, impl.getHandlers().size(), "Handler should be unregistered");
        assertEquals(handler2, impl.getHandlers().get(0), "The correct handler was not unregistered");
        impl.unTap(handler2);
        assertEquals(0, impl.getHandlers().size(), "Last handler not unregistered");
    }

    @Test
    void unTapAll() {
        final var impl = new Impl<Consumer<Object>>();
        final int registrationCount = 5;
        for (int i = 0; i < registrationCount; i++) {
            impl.tap(Object::notify);
        }
        assertEquals(registrationCount, impl.getHandlers().size(), "Registration count is wrong after setup");
        assertDoesNotThrow(impl::unTapAll);
        assertEquals(0, impl.getHandlers().size(), "The handler list should be empty");
        impl.tap(Object::notify);
        assertEquals(1, impl.getHandlers().size(), "Handler should be able to register after unTapAll()");
    }
}