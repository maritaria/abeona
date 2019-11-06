package abeona.demos.knapsack;

import abeona.Query;
import abeona.Transition;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Stream;

class KnapsackPuzzleTest {
    private void solveWithQuery(Query<KnapsackFilling> query) {
        // Setup puzzle
        final var puzzle = new KnapsackPuzzle(5);
        puzzle.availableItems.add(new Item(1, 1));
        puzzle.availableItems.add(new Item(2, 2));
        puzzle.availableItems.add(new Item(3, 3));
        puzzle.availableItems.add(new Item(4, 1));
        puzzle.availableItems.add(new Item(1, 2));
        puzzle.availableItems.add(new Item(2, 3));
        puzzle.availableItems.add(new Item(3, 1));
        puzzle.availableItems.add(new Item(4, 2));
        query.getFrontier().add(Stream.of(new KnapsackFilling(puzzle, Collections.emptySet())));
        // Setup answer collection
        final var answers = new HashSet<KnapsackFilling>();
        query.onTransitionEvaluation.tap(event -> {
            answers.add(event.getTransition().getTargetState());
            final var source = event.getTransition().getSourceState();
            answers.remove(source);
        });
        // Run exploration
        query.explore();
        // Print results
        final var top5 = answers.stream().sorted(Comparator.comparingInt(KnapsackFilling::totalValue).reversed()).limit(
                5).iterator();
        System.out.println("Top 5 solutions: ");
        while (top5.hasNext()) {
            System.out.println(top5.next());
        }
    }

    @Test
    void solveWithBfs() {
        final var query = new Query<KnapsackFilling>(QueueFrontier.lifoFrontier(),
                new HashSetHeap<>(),
                state -> state.next().map(next -> new Transition<>(state, next)));
        solveWithQuery(query);
    }
}