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
    }
}
