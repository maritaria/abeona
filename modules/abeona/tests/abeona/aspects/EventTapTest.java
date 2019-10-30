package abeona.aspects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class EventTapTest {
    @Test
    void constructor() {
        assertDoesNotThrow(() -> new EventTap<>());
    }

    @Test
    @Tag("accept")
    void accept() {
        final var tap = new EventTap<>();
        assertThrows(IllegalArgumentException.class, () -> tap.accept(null));
        assertDoesNotThrow(() -> tap.accept(new Object()));
    }

    @Test
    @Tag("accept")
    void accept_callbackOrder() {
        final int handlerCount = 5;
        final var tap = new EventTap<>();
        List<Boolean> flags = new ArrayList<>(5);
        for (int i = 0; i < handlerCount; i++) {
            final int localI = i;
            flags.add(false);
            tap.tap(unused -> {
                for (int j = 0; j < localI; j++) {
                    assertTrue(flags.get(j), "Handler #" + j + " should have been invoked already");
                }
                for (int j = localI; j < flags.size(); j++) {
                    assertFalse(flags.get(j), "Handler #" + j + " has been invoked too early");
                }
                flags.set(localI, true);
            });
        }
        tap.accept(new Object());
        assertThrows(Throwable.class, () -> tap.accept(new Object()), "The assertions should throw on running a second time");
    }

    @Test
    @Tag("accept")
    void accept_duplicateHandlers() {
        final int handlerCount = 5;
        final var event = new EventTap<AtomicInteger>();
        AtomicInteger counter = new AtomicInteger(0);
        Consumer<AtomicInteger> handler = AtomicInteger::getAndIncrement;
        for (int i = 0; i < handlerCount; i++) {
            event.tap(handler);
        }
        assertEquals(0, counter.get(), "counter changed before triggering event");
        event.accept(counter);
        assertEquals(handlerCount, counter.get(), "counter should be equal to handler count");
    }

    @Test
    @Tag("accept")
    void accept_mutationException() {
        final var event = new EventTap<>();
        final int handlerCount = 5;
        for (int i = 0; i < handlerCount; i++) {
            event.tap(unused -> {
                event.tap(inner -> {
                });
            });
        }
        assertThrows(ConcurrentModificationException.class, () -> event.accept(event), "Mutation of the EventTap should not be allowed during event execution");
    }

    @Test
    void unTap() {
        Consumer<Object> handler = unused -> {
            throw new AssertionError("should not be invoked");
        };
        final var event = new EventTap<>();
        event.tap(handler);
        assertThrows(AssertionError.class, () -> event.accept(new Object()), "The handler should have been invoked and thrown an error");
        event.unTap(handler);
        assertDoesNotThrow(() -> event.accept(new Object()), "The handler should not have been invoked");
    }

}