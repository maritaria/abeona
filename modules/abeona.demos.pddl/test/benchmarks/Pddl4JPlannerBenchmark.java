package benchmarks;

import fr.uga.pddl4j.planners.Planner;

public abstract class Pddl4JPlannerBenchmark extends BenchmarkItem {

    protected abstract Planner setupPlanner();

    private Planner planner;

    @Override
    public final void setupBenchmark() {
        super.setupBenchmark();
        planner = setupPlanner();
    }

    @Override
    public final void run() {
        final var plan = planner.search(problem);
        if (plan == null ) {
            throw new RuntimeException("Failed to create a plan");
        }
    }
}
