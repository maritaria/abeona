package abeona.demos.staircase;

import abeona.util.Arguments;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Staircase {
    private final int length;
    private final int[] steps;

    public Staircase(int length, int... steps) {
        Arguments.requireMinimum(0, length, "length");
        Arguments.requireNonNull(steps, "steps");
        Arguments.requireMinimum(1, steps.length, "steps.length");
        this.length = length;
        this.steps = Arrays.copyOf(steps, steps.length);
    }

    Stream<Integer> next(int position) {
        return IntStream.of(steps).map(i -> position + i).filter(pos -> pos >= 0).filter(pos -> pos <= length).boxed();
    }

    boolean isGoal(Integer position) {
        return position == length;
    }
}
