package abeona.demos.goatgame;

import abeona.NextFunction;
import abeona.Query;
import abeona.frontiers.ManagedFrontier;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.Stream;

class GoatWindow extends JFrame implements ActionListener {
    static Query<GameState> createQuery() {
        // Pick the frontier to use
        final var frontier = QueueFrontier.<GameState>fifoFrontier();

        // Pick the heap to use
        final var heap = new HashSetHeap<GameState>();

        // Pick the next-function
        final var next = NextFunction.wrap(GameState::next);

        // Build the query
        final var query = new Query<>(frontier, heap, next);

        // You can add behaviours here

        return query;
    }

    private final GameView gameView = new GameView(null);
    private final FrontierView frontierView = new FrontierView();
    private final JButton resetButton = new JButton("Reset");
    private final JButton nextButton = new JButton("Next");
    private Query<GameState> query = createQuery();

    GoatWindow() {
        setPreferredSize(new Dimension(400, 300));
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        // GameView
        add(gameView, BorderLayout.CENTER);
        // FrontierView
        frontierView.onSelect = gameView::setGameState;
        add(frontierView, BorderLayout.EAST);
        // Buttons
        final var buttonPanel = new JPanel(new FlowLayout());
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);
        nextButton.addActionListener(this);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);
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
        query = createQuery();
        query.getFrontier().add(Stream.of(new GameState()));
        updateViews();
    }

    private void onNext() {
        query.exploreNext();
        updateViews();
    }

    private void updateViews() {
        final var frontier = query.getFrontier();
        if (frontier instanceof ManagedFrontier) {
            final var managed = (ManagedFrontier<GameState>) frontier;
            frontierView.setFrontier(managed);
        } else {
            frontierView.setFrontier(null);
        }
        validate();
        repaint();
    }
}
