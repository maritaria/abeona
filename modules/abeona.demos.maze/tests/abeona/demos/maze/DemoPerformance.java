package abeona.demos.maze;

import org.junit.jupiter.api.Test;

import java.util.List;

class DemoPerformance {
    @Test
    void runAllPerformanceDemos() {
        List<DemoBase> demos = List.of(
                new DemoBfs(),
                new DemoDfs(),
                new DemoDijkstra(),
                new DemoBestFirst()
        );
        for (DemoBase demo : demos) {
            demo.measureRuntimePerformance();
        }
        DemoBase.OPTIMIZE = true;
        System.out.println("Now with optimizations:");
        for (DemoBase demo : demos) {
            demo.measureRuntimePerformance();
        }
    }

    @Test
    void runPerformanceSeries() {
        final var sizes = List.of(25,50,75,100,125,150,175,200,225,250);
        final var demos = List.of(
                new DemoBfs(),
                new DemoDfs(),
                new DemoDijkstra(),
                new DemoBestFirst()
        );
        for (var demo : demos) {
            System.out.println("Running suite for " + demo.getClass().getName());
            for (var size : sizes) {
                System.out.println("Size: " + size);
                DemoBase.setSize(size);
                demo.measureRuntimePerformance();
            }
        }
    }
}
