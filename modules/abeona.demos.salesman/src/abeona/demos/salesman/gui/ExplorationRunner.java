package abeona.demos.salesman.gui;

import abeona.Query;
import abeona.State;
import abeona.util.Arguments;

public class ExplorationRunner<StateType extends State> {
    private final ManualStepBehaviour<StateType> stepBehaviour = new ManualStepBehaviour<>();
    private final Query<StateType> query;
    private final Thread runner;
    private volatile boolean stopped = false;

    public ExplorationRunner(Query<StateType> query) {
        Arguments.requireNonNull(query, "query");
        this.query = query;
        this.stepBehaviour.attach(query);
        this.runner = new Thread(this::runnerMain, "ExplorationRunner");
        this.runner.setDaemon(true);
        this.runner.start();
    }

    private void runnerMain() {
        while (!stopped) {
            query.explore();
        }
    }

    public void nextStep(int stepCount) {
        stepBehaviour.next(this.query, stepCount);
    }

    public void stop() {
        stopped = true;
        stepBehaviour.stop();
    }
}
