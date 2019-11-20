package abeona.demos.salesman;

import abeona.NextFunction;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SalesmanPathTest {

    @Test
    void testBfs() {
        final var frontier = QueueFrontier.<SalesmanPath>fifoFrontier();
        final var heap = new HashSetHeap<SalesmanPath>();
        final var query = new Query(frontier, heap, NextFunction.wrap(SalesmanPath::next));
    }

}