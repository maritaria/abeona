package abeona.demos.salesman.gui;

import abeona.ExplorationQuery;
import abeona.State;
import abeona.TerminateExplorationSignal;
import abeona.behaviours.AbstractBehaviour;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ManualStepBehaviour<StateType extends State> extends AbstractBehaviour<StateType> {
    private final Map<ExplorationQuery<StateType>, AtomicBoolean> continueFlags = new WeakHashMap<>(1);
    private final Map<ExplorationQuery<StateType>, AtomicBoolean> doneSignals = new WeakHashMap<>(1);
    private final Map<ExplorationQuery<StateType>, AtomicBoolean> stopFlags = new WeakHashMap<>(1);

    @Override
    public void attach(ExplorationQuery<StateType> explorationQuery) {
        final var continueFlag = new AtomicBoolean(false);
        final var doneSignal = new AtomicBoolean(false);
        final var stopFlag = new AtomicBoolean(false);
        tapQueryBehaviour(explorationQuery,
                explorationQuery.beforeStatePicked,
                event -> this.beforeStatePicked(continueFlag, stopFlag));
        tapQueryBehaviour(explorationQuery, explorationQuery.afterStateEvaluation, event -> doneSignal.set(true));
        tapQueryBehaviour(explorationQuery, explorationQuery.afterExploration, event -> doneSignal.set(true));
        continueFlags.put(explorationQuery, continueFlag);
        doneSignals.put(explorationQuery, doneSignal);
        stopFlags.put(explorationQuery, stopFlag);
    }

    private void beforeStatePicked(AtomicBoolean continueFlag, AtomicBoolean stopFlag) {
        try {
            while (!continueFlag.getAndSet(false)) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (stopFlag.get()) {
            throw new TerminateExplorationSignal();
        }
    }

    void next(ExplorationQuery<StateType> query) {
        final var flag = continueFlags.get(query);
        final var done = doneSignals.get(query);
        if (flag != null) {
            done.set(false);
            flag.set(true);
            try {
                while (!done.get()) {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            done.set(false);
        }
    }

    public void stop() {
        stopFlags.values().forEach(flag -> flag.set(true));
        continueFlags.values().forEach(flag -> flag.set(true));
    }
}
