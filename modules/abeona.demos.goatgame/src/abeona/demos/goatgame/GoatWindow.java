package abeona.demos.goatgame;

import abeona.Query;
import abeona.TerminationType;
import abeona.demo.common.QueryView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.Stream;

class GoatWindow extends JFrame implements ActionListener {
    private final QueryView<GameState> queryView = new QueryView<>(GoatView::new);
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
        add(queryView, BorderLayout.CENTER);
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
        query.getFrontier().add(Stream.of(new GameState()));
        queryView.setQuery(query);
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
