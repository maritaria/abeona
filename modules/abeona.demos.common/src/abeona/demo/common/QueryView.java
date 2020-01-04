package abeona.demo.common;

import abeona.Query;
import abeona.State;
import abeona.behaviours.AbstractBehaviour;
import abeona.behaviours.SweepLineBehaviour;
import abeona.frontiers.ManagedFrontier;
import abeona.heaps.ManagedHeap;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.function.Function;

public class QueryView<StateType extends State> extends JPanel {
    private final StateListView<StateType> frontierBeforeView;
    private final StateListView<StateType> nextStateView;
    private final StateListView<StateType> neighboursView;
    private final StateListView<StateType> discoveriesView;
    private final StateListView<StateType> frontierAfterView;
    private final StateListView<StateType> purgedView;
    private final StateListView<StateType> heapView;
    private boolean frontiersEnabled = true;

    public QueryView(Function<StateType, JPanel> viewCreator) {
        this.frontierBeforeView = new StateListView<StateType>("Frontier (before)", viewCreator);
        this.nextStateView = new StateListView<>("Picked state", viewCreator);
        this.neighboursView = new StateListView<>("Neighbours", viewCreator);
        this.discoveriesView = new StateListView<>("Discoveries", viewCreator);
        this.frontierAfterView = new StateListView<>("Frontier (after)", viewCreator);
        this.purgedView = new StateListView<>("Purged", viewCreator);
        this.heapView = new StateListView<>("Heap (after)", viewCreator);
        setLayout(new GridLayout(1, 10));
        add(frontierBeforeView);
        add(nextStateView);
        add(neighboursView);
        add(discoveriesView);
        add(frontierAfterView);
    }

    public void setQuery(Query<StateType> query) {
        final var frontier = query.getFrontier() instanceof ManagedFrontier ? (ManagedFrontier<StateType>) query.getFrontier() : null;
        final var heap = query.getHeap() instanceof ManagedHeap ? (ManagedHeap<StateType>) query.getHeap() : null;
        query.addBehaviour(new AbstractBehaviour<>() {
            @SuppressWarnings("unchecked")
            @Override
            public void attach(Query<StateType> query) {
                if (frontier != null) {
                    add(frontierBeforeView, 0);
                    add(frontierAfterView, getComponentZOrder(discoveriesView) + 1);
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
                } else {
                    remove(frontierBeforeView);
                    remove(frontierAfterView);
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
                    frontierBeforeView.highlightState(stateEvent.getState(), Color.red);
                    neighboursView.clearStates();
                    discoveriesView.clearStates();
                });
                tapQueryBehaviour(query, query.onTransitionEvaluation, evaluation -> {
                    neighboursView.addState(evaluation.getTransition().getTargetState());
                });
                tapQueryBehaviour(query, query.onStateDiscovery, discovery -> {
                    neighboursView.highlightState(discovery.getTransition().getTargetState(), Color.green);
                    discoveriesView.addState(discovery.getTransition().getTargetState());
                });
                query.getBehaviours(SweepLineBehaviour.class).findFirst().ifPresentOrElse(behaviour -> {
                    add(purgedView, QueryView.this.getComponentZOrder(nextStateView) + 1);
                    final var sweepLine = (SweepLineBehaviour<StateType>) behaviour;
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
