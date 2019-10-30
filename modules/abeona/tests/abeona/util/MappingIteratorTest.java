package abeona.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MappingIteratorTest {

    @SuppressWarnings("unchecked")
    private static Iterator<Integer> mockIterator() {
        return (Iterator<Integer>) mock(Iterator.class);
    }

    @SuppressWarnings("unchecked")
    private static Function<Integer, Integer> mockTransformer() {
        return (Function<Integer, Integer>) mock(Function.class);
    }

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new MappingIterator<>(null, null));
        assertThrows(IllegalArgumentException.class, () -> new MappingIterator<>(Collections.emptyIterator(), null));
        assertThrows(IllegalArgumentException.class, () -> new MappingIterator<>(null, Object::hashCode));
        assertDoesNotThrow(() -> new MappingIterator<>(Collections.emptyIterator(), Object::hashCode));
    }

    @Test
    void remove() {
        final var source = mockIterator();
        when(source.hasNext()).thenReturn(true);
        when(source.next()).thenReturn(1);
        final var subject = new MappingIterator<>(source, Function.identity());
        subject.next();
        assertDoesNotThrow(subject::remove);
        verify(source, times(1)).remove();
    }

    @Test
    void hasNext() {
        final var source = mockIterator();
        when(source.hasNext()).thenReturn(true, false);
        final var subject = new MappingIterator<>(source, Function.identity());
        verify(source, times(0)).hasNext();
        assertTrue(subject.hasNext());
        verify(source, times(1)).hasNext();
        assertFalse(subject.hasNext());
        verify(source, times(2)).hasNext();
    }

    @Test
    void next() {
        final var iterator = mockIterator();
        when(iterator.next()).thenReturn(1, 2, 3, 4);
        final var transformer = mockTransformer();

        when(transformer.apply(any())).thenReturn(2, 4, 6, 8);

        final var subject = new MappingIterator<>(iterator, transformer);

        assertEquals(2, subject.next());
        assertEquals(4, subject.next());
        assertEquals(6, subject.next());
        assertEquals(8, subject.next());

        verify(iterator, times(4)).next();
        verify(transformer, times(4)).apply(any());
    }
}