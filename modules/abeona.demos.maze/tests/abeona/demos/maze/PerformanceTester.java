package abeona.demos.maze;

public final class PerformanceTester {
    static void measureRuntime(Runnable runnable) {
        for (int i = 0; i < 5; i++) {
            runnable.run();
        }
        int repeats = 5;
        long start = System.currentTimeMillis();
        for (int i = 0; i < repeats; i++) {
            runnable.run();
        }
        long end = System.currentTimeMillis();
        long duration = (end - start) / repeats;

        System.out.println("Average over " + repeats + " runs: " + duration + "ms (" + (duration / 1000) + "s)");
    }
}
