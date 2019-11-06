package abeona.demos.salesman.gui;

import abeona.ExplorationQuery;
import abeona.State;
import abeona.TerminateExplorationSignal;
import abeona.behaviours.AbstractBehaviour;
import abeona.util.Arguments;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class ManualStepBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final Map<ExplorationQuery<StateType>, AtomicInteger> continueFlags = new WeakHashMap<>(1);
    private final Map<ExplorationQuery<StateType>, AtomicInteger> doneSignals = new WeakHashMap<>(1);
    private final Map<ExplorationQuery<StateType>, AtomicBoolean> stopFlags = new WeakHashMap<>(1);

    @Override
    public void attach(ExplorationQuery<StateType> explorationQuery) {
        final var continueFlag = new AtomicInteger(0);
        final var doneSignal = new AtomicInteger(0);
        final var stopFlag = new AtomicBoolean(false);
        tapQueryBehaviour(explorationQuery,
                explorationQuery.beforeStatePicked,
                event -> this.beforeStatePicked(continueFlag, stopFlag));
        tapQueryBehaviour(explorationQuery, explorationQuery.afterStateEvaluation, event -> doneSignal.decrementAndGet());
        tapQueryBehaviour(explorationQuery, explorationQuery.afterExploration, event -> doneSignal.set(0));
        continueFlags.put(explorationQuery, continueFlag);
        doneSignals.put(explorationQuery, doneSignal);
        stopFlags.put(explorationQuery, stopFlag);
    }

    private void beforeStatePicked(AtomicInteger continueFlag, AtomicBoolean stopFlag) {
        try {
            while (continueFlag.getAndUpdate(i -> i <= 0 ? 0 : i - 1) <= 0) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (stopFlag.get()) {
            throw new TerminateExplorationSignal();
        }
    }

    void next(ExplorationQuery<StateType> query, int steps) {
        Arguments.requireMinimum(1, steps, "steps");
        final var flag = continueFlags.get(query);
        final var done = doneSignals.get(query);
        if (flag != null) {
            done.addAndGet(steps);
            flag.addAndGet(steps);
            try {
                while (done.get() > 0) {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        stopFlags.values().forEach(flag -> flag.set(true));
        continueFlags.values().forEach(flag -> flag.set(100));
    }
}
