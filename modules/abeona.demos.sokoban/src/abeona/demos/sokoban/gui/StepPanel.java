package abeona.demos.sokoban.gui;

import abeona.Query;
import abeona.behaviours.AbstractBehaviour;
import abeona.behaviours.SweepLineBehaviour;
import abeona.demos.sokoban.SokobanState;
import abeona.frontiers.ManagedFrontier;
import abeona.heaps.ManagedHeap;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

public class StepPanel extends JPanel {
    private final StateListView frontierBeforeView = new StateListView("Frontier before");
    private final StateListView nextStateView = new StateListView("Picked state");
    private final StateListView neighboursView = new StateListView("Neighbours");
    private final StateListView discoveriesView = new StateListView("Discoveries");
    private final StateListView frontierAfterView = new StateListView("Frontier after");
    private final StateListView purgedView = new StateListView("Purged");
    private final StateListView heapView = new StateListView("Heap");
    private boolean frontiersEnabled = true;

    public StepPanel() {
        setLayout(new GridLayout(1, 10));
        add(frontierBeforeView);
        add(nextStateView);
        add(purgedView);
        // add(neighboursView);
        add(discoveriesView);
        add(frontierAfterView);
        // add(heapView);
    }

    void setQuery(Query<SokobanState> query) {
        final var frontier = query.getFrontier() instanceof ManagedFrontier ? (ManagedFrontier<SokobanState>) query.getFrontier() : null;
        final var heap = query.getHeap() instanceof ManagedHeap ? (ManagedHeap<SokobanState>) query.getHeap() : null;
        query.addBehaviour(new AbstractBehaviour<>() {
            @SuppressWarnings("unchecked")
            @Override
            public void attach(Query<SokobanState> query) {
                if (frontier != null) {
                    tapQueryBehaviour(query, query.beforeStatePicked, unused -> {
                        if (frontiersEnabled) {
                            frontierBeforeView.setStates(frontier);
                        }

                    });
                    tapQueryBehaviour(query, query.afterStateEvaluation, unused -> {
                        if (frontiersEnabled) {
                            frontierAfterView.setStates(frontier);
                        }
                    });
                }
                if (heap != null) {
                    add(heapView, getComponentZOrder(frontierAfterView) + 1);
                    tapQueryBehaviour(query, query.afterStateEvaluation, unused -> {
                        if (frontiersEnabled) {
                            heapView.setStates(heap);
                        }
                    });
                } else {
                    remove(heapView);
                }
                tapQueryBehaviour(query, query.afterStatePicked, stateEvent -> {
                    nextStateView.setStates(Collections.singleton(stateEvent.getState()));
                    neighboursView.clearStates();
                    discoveriesView.clearStates();
                });
                tapQueryBehaviour(query, query.onTransitionEvaluation, evaluation -> {
                    neighboursView.addState(evaluation.getTransition().getTargetState());
                });
                tapQueryBehaviour(query, query.onStateDiscovery, discovery -> {
                    discoveriesView.addState(discovery.getTransition().getTargetState());
                });
                query.getBehaviours(SweepLineBehaviour.class).findFirst().ifPresentOrElse(behaviour -> {
                    add(purgedView, StepPanel.this.getComponentZOrder(nextStateView) + 1);
                    final var sweepLine = (SweepLineBehaviour<SokobanState>) behaviour;
                    tapForeignBehaviour(sweepLine.onPurge, purge -> {
                        purgedView.addState(purge.getState());
                    });
                    tapQueryBehaviour(query, query.beforeStatePicked, unused -> {
                        purgedView.clearStates();
                    });
                }, () -> {
                    remove(purgedView);
                });
            }
        });
        frontierBeforeView.setStates(frontier);
        nextStateView.clearStates();
        neighboursView.clearStates();
        discoveriesView.clearStates();
        frontierAfterView.clearStates();
        purgedView.clearStates();
        heapView.clearStates();
        updateUI();
    }

    public void setFrontiersEnabled(boolean value) {
        this.frontiersEnabled = value;
        frontierBeforeView.setVisible(value);
        frontierAfterView.setVisible(value);
        heapView.setVisible(value);
        if (!value) {
            frontierBeforeView.clearStates();
            frontierAfterView.clearStates();
            purgedView.clearStates();
            heapView.clearStates();
        }
    }
}
