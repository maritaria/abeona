package abeona.demos.sokoban.gui;

import abeona.Query;
import abeona.behaviours.AbstractBehaviour;
import abeona.demos.sokoban.SokobanState;
import abeona.frontiers.ManagedFrontier;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

public class StepPanel extends JPanel {
    private final StateListView frontierBeforeView = new StateListView("Frontier before");
    private final StateListView nextStateView = new StateListView("Picked state");
    private final StateListView neighboursView = new StateListView("Neighbours");
    private final StateListView discoveriesView = new StateListView("Discoveries");
    private final StateListView frontierAfterView = new StateListView("Frontier after");
    private boolean frontiersEnabled = true;

    public StepPanel() {
        setLayout(new GridLayout(1, 5));
        add(frontierBeforeView);
        add(nextStateView);
        add(neighboursView);
        add(discoveriesView);
        add(frontierAfterView);
    }

    void setQuery(Query<SokobanState> query) {
        final var frontier = query.getFrontier() instanceof ManagedFrontier ? (ManagedFrontier<SokobanState>) query.getFrontier() : null;
        query.addBehaviour(new AbstractBehaviour<>() {
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
            }
        });
        frontierBeforeView.setStates(frontier);
        nextStateView.clearStates();
        neighboursView.clearStates();
        discoveriesView.clearStates();
        frontierAfterView.clearStates();
        updateUI();
    }

    public void setFrontiersEnabled(boolean value) {
        this.frontiersEnabled = value;
        frontierBeforeView.setVisible(value);
        frontierAfterView.setVisible(value);
        if (!value) {
            frontierBeforeView.clearStates();
            frontierAfterView.clearStates();
        }
    }
}
