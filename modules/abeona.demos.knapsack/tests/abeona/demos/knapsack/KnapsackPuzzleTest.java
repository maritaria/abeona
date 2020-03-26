package abeona.demos.knapsack;

import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.SweepLineBehaviour;
import abeona.frontiers.Frontier;
import abeona.frontiers.ManagedFrontier;
import abeona.frontiers.QueueFrontier;
import abeona.frontiers.TreeMapFrontier;
import abeona.heaps.HashSetHeap;
import abeona.heaps.Heap;
import abeona.heaps.ManagedHeap;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

class KnapsackPuzzleTest {

    @Test
    void solveWithBfs() {
        final Frontier<KnapsackFilling> frontier = QueueFrontier.fifoFrontier();
        final Heap<KnapsackFilling> heap = new HashSetHeap<>();
        final NextFunction<KnapsackFilling> next = NextFunction.wrap(KnapsackFilling::next);
        final Query<KnapsackFilling> query = new Query<>(frontier, heap, next);
        solveWithQuery(query);
    }

    @Test
    void solveWithDfs() {
        final Frontier<KnapsackFilling> frontier = QueueFrontier.lifoFrontier();
        final Heap<KnapsackFilling> heap = new HashSetHeap<>();
        final NextFunction<KnapsackFilling> next = NextFunction.wrap(KnapsackFilling::next);
        final Query<KnapsackFilling> query = new Query<>(frontier, heap, next);
        solveWithQuery(query);
    }

    @Test
    void solveWithGreedy() {
        final Comparator<KnapsackFilling> comp = Comparator.comparingLong(filling -> filling.getItems().count());
        final Frontier<KnapsackFilling> frontier = TreeMapFrontier.withCollisions(comp, KnapsackFilling::hashCode);
        final Heap<KnapsackFilling> heap = new HashSetHeap<>();
        final NextFunction<KnapsackFilling> next = NextFunction.wrap(KnapsackFilling::next);
        final Query<KnapsackFilling> query = new Query<>(frontier, heap, next);
        solveWithQuery(query);
    }

    @Test
    void solveWithSweepLine() {
        final Comparator<KnapsackFilling> progressComparator = Comparator
                .comparingLong(filling -> filling.getItems().count());
        final Comparator<KnapsackFilling> frontierComparator = progressComparator
                .thenComparingInt(KnapsackFilling::totalValue).thenComparingInt(KnapsackFilling::totalWeight);
        final Frontier<KnapsackFilling> frontier = TreeMapFrontier.withExactOrdering(frontierComparator);
        final Heap<KnapsackFilling> heap = new HashSetHeap<>();
        final NextFunction<KnapsackFilling> next = NextFunction.wrap(KnapsackFilling::next);
        final Query<KnapsackFilling> query = new Query<>(frontier, heap, next);
        query.addBehaviour(new SweepLineBehaviour<>(progressComparator));
        solveWithQuery(query);
    }

    @Test
    void solveWithoutSweepLine() {
        final Comparator<KnapsackFilling> progressComparator = Comparator
                .comparingLong(filling -> filling.getItems().count());
        final Comparator<KnapsackFilling> frontierComparator = progressComparator
                .thenComparingInt(KnapsackFilling::totalValue).thenComparingInt(KnapsackFilling::totalWeight);
        final Frontier<KnapsackFilling> frontier = TreeMapFrontier.withExactOrdering(frontierComparator);
        final Heap<KnapsackFilling> heap = new HashSetHeap<>();
        final NextFunction<KnapsackFilling> next = NextFunction.wrap(KnapsackFilling::next);
        final Query<KnapsackFilling> query = new Query<>(frontier, heap, next);
        solveWithQuery(query);
    }

    private void solveWithQuery(Query<KnapsackFilling> query) {
        // Setup puzzle
        final var puzzle = new KnapsackPuzzle(50);
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                puzzle.availableItems.add(new Item(i, j));
            }
        }
        query.getFrontier().add(Stream.of(new KnapsackFilling(puzzle, Collections.emptySet())));
        // Setup answer collection
        final var answers = new HashSet<KnapsackFilling>();
        query.onTransitionEvaluation.tap(event -> {
            answers.add(event.getTransition().getTargetState());
            final var source = event.getTransition().getSourceState();
            answers.remove(source);
        });
        final var frontier = (ManagedFrontier<KnapsackFilling>) query.getFrontier();
        final var heap = (ManagedHeap<KnapsackFilling>) query.getHeap();
        query.afterStateEvaluation.tap(event -> {
            System.out.println(heap.size());
            // System.out.println(heap.size() + ", " + frontier.size());
            //            System.out.println("Heap: " + heap.size());
            //            heap.forEach(System.out::println);
            //            System.out.println("Frontier: " + frontier.size());
            //            frontier.forEach(System.out::println);
        });
        // Run exploration
        query.explore();
        // Print results
        // System.out.println("Top 5 solutions: ");
        // answers.stream().sorted(Comparator.comparingInt(KnapsackFilling::totalValue).reversed()).limit(5)
        //         .forEach(System.out::println);

        // System.out.println("Heap size over time:");
    }
}