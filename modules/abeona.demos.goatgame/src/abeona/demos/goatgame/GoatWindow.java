package abeona.demos.goatgame;

import abeona.Query;
import abeona.TerminationType;
import abeona.behaviours.AbstractBehaviour;
import abeona.frontiers.ManagedFrontier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.stream.Stream;

class GoatWindow extends JFrame implements ActionListener {
    private final JPanel columns = new JPanel();
    private final StateListView frontierBeforeView = new StateListView("Frontier (before)");
    private final StateListView frontierAfterView = new StateListView("Frontier (after)");
    private final StateListView nextStateView = new StateListView("Next state");
    private final StateListView neighboursView = new StateListView("Neighbours");
    private final StateListView discoveriesView = new StateListView("Discoveries");
    private final JButton resetButton = new JButton("Reset");
    private final JButton nextButton = new JButton("Next");
    private final JLabel terminationLabel = new JLabel();
    private Query<GameState> query = null;

    GoatWindow() {
        setPreferredSize(new Dimension(1300, 480));
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        // GameView
        add(columns, BorderLayout.CENTER);
        columns.setLayout(new GridLayout(1, 5));
        columns.add(frontierBeforeView);
        columns.add(nextStateView);
        columns.add(neighboursView);
        columns.add(discoveriesView);
        columns.add(frontierAfterView);
        // Buttons
        final var buttonPanel = new JPanel(new FlowLayout());
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);
        nextButton.addActionListener(this);
        buttonPanel.add(nextButton);
        buttonPanel.add(terminationLabel);
        add(buttonPanel, BorderLayout.SOUTH);
        onReset();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == resetButton) {
            onReset();
        } else if (actionEvent.getSource() == nextButton) {
            onNext();
        }
    }

    private void onReset() {
        query = GoatProgram.createQuery();
        final var frontier = query.getFrontier() instanceof ManagedFrontier ? (ManagedFrontier<GameState>) query.getFrontier() : null;
        query.getFrontier().add(Stream.of(new GameState()));
        query.addBehaviour(new AbstractBehaviour<>() {
            @Override
            public void attach(Query<GameState> query) {
                if (frontier != null) {
                    tapQueryBehaviour(query, query.beforeStatePicked, unused -> {
                        frontierBeforeView.setStates(frontier);

                    });
                    tapQueryBehaviour(query, query.afterStateEvaluation, unused -> {
                        frontierAfterView.setStates(frontier);
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
        nextButton.setEnabled(true);
        terminationLabel.setVisible(false);
        validate();
        repaint();
    }

    private void onNext() {
        final var termination = query.exploreNext();
        termination.ifPresentOrElse(type -> {
            terminationLabel.setText("Exploration terminated: " + type.toString());
            terminationLabel.setVisible(true);
            if (type != TerminationType.ManualTermination) {
                nextButton.setEnabled(false);
            }
        }, () -> {
            terminationLabel.setVisible(false);
            nextButton.setEnabled(true);
        });
    }
}
