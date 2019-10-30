package abeona.aspects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class FunctionTapTest {
    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new FunctionTap<>(null));
        assertDoesNotThrow(() -> new FunctionTap<>(Function.identity()));
        new FunctionTap<>(o -> {
            // TODO: Use fail() in EventTapTest
            fail("The original function should not be invoked during tap creation");
            return 0;
        });
    }

    @Test
    void apply() {
        final var tap = new FunctionTap<>(Function.identity());
        assertThrows(IllegalArgumentException.class, () -> tap.apply(null));
    }

    @Test
    void apply_invokeOriginal() {
        AtomicBoolean originalCalled = new AtomicBoolean(false);
        final var tap = new FunctionTap<>(obj -> originalCalled.getAndSet(true));
        tap.apply(new Object());
        assertTrue(originalCalled.getAndSet(false), "Original not called with no interceptors added");
        tap.tap((o, next) -> next.apply(o));
        assertFalse(originalCalled.get(), "Original should not be called from .tap() call");
        tap.apply(new Object());
        assertTrue(originalCalled.getAndSet(false), "Original not called with pass-through interceptor");
    }

    @Test
    void apply_interceptorOrder() {
        final var tap = new FunctionTap<>(Function.identity());
        final int interceptorCount = 5;
        List<Boolean> flags = IntStream.range(0, interceptorCount)
                .mapToObj(i -> false)
                .collect(Collectors.toList());

        for (int i = 0; i < flags.size(); i++) {
            final int handlerIndex = i;
            tap.tap((obj, next) -> {
                for (Boolean flag : flags) {
                    assertFalse(flag, "None of the interceptors should have fired yet");
                }
                next.apply(obj);
                for (int j = 0; j < flags.size(); j++) {
                    if (j < handlerIndex) {
                        assertTrue(flags.get(j), "Early registered interceptors should have finished already");
                    } else {
                        assertFalse(flags.get(j), "Late registered interceptors should not have finished yet");
                    }
                }
                flags.set(handlerIndex, true);
                return obj;
            });
        }
    }

    @Test
    void apply_interceptorArgs() {
        // TODO: Unit test modification of the arguments by an interceptor
        fail("Not implemented");
    }

    @Test
    void apply_interceptorResult() {
        // TODO: Unit test modification of the return value by an interceptor
        fail("Not implemented");
    }

    @Test
    void apply_order() {
        AtomicBoolean original = new AtomicBoolean(false);
        AtomicBoolean first = new AtomicBoolean(false);
        AtomicBoolean second = new AtomicBoolean(false);

        Object argFromCall = new Object();
        Object argFromFirst = new Object();
        Object resultFromOriginal = new Object();
        Object resultFromSecond = new Object();

        // Original logic
        final var tap = new FunctionTap<>(obj -> {
            assertEquals(argFromFirst, obj, "The first handler should have modified the result");
            assertFalse(original.getAndSet(true), "Original handler should not be invoked twice");
            assertFalse(first.get(), "the first handler should not have completed yet");
            assertFalse(second.get(), "the second handler should not have completed yet");
            return resultFromOriginal;
        });

        // First registered handler
        tap.tap((obj, next) -> {
            assertNotNull(next, "next should not be null");
            assertEquals(argFromCall, obj, "First handler should receive event args modified by handler 2");
            assertFalse(original.get(), "Original handler should not be invoked yet");
            assertFalse(first.get(), "The first registered handler should not be invoked twice");
            assertFalse(second.get(), "The second registered handler should not be completed yet");
            var result = next.apply(argFromFirst);
            assertTrue(original.get(), "original must be invoked after calling next() in handler 1");
            assertFalse(first.getAndSet(true), "handler 1 must be invoked after calling next() in handler 1");
            assertFalse(second.get(), "second must not have completed yet after next() in handler 1");
            assertEquals(resultFromOriginal, result, "First received wrong result from original logic");
            return result;
        });

        // Second handler
        tap.tap((obj, next) -> {
            assertNotNull(next, "next should not be null");
            assertEquals(argFromCall, obj, "Handler 2 should receive original event args");
            assertFalse(original.get(), "Original handler should not be invoked yet");
            assertFalse(original.get(), "Handler 1 should not be invoked yet");
            assertFalse(second.get(), "Handler 2 should not be invoked twice");
            var result = next.apply(obj);
            assertTrue(original.get(), "original must be invoked after calling next() in handler 2");
            assertTrue(first.get(), "handler 1 must be invoked after calling next() in handler 2");
            assertFalse(second.getAndSet(true), "handler 2 must not be called twice after next() in handler 2");
            assertEquals(resultFromOriginal, result, "First received wrong result from original logic");
            return resultFromSecond;
        });

        // Run event
        var result = tap.apply(argFromCall);
        assertTrue(original.get());
        assertTrue(first.get());
        assertTrue(second.get());
        assertEquals(resultFromSecond, result, "handler 2 change to result did not propagate");
    }
}