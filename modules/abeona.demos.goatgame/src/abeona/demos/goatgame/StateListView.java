package abeona.demos.goatgame;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

class StateListView extends JScrollPane {
    Consumer<GameState> onSelect;
    private final JPanel stack = new JPanel();

    StateListView(String title) {
        setViewportView(stack);
        if (title != null) {
            setBorder(BorderFactory.createTitledBorder(title));
        }
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(300, 400));
    }

    void setStates(Iterable<GameState> frontier) {
        stack.removeAll();
        if (frontier != null) {
            for (GameState state : frontier) {
                addStateImpl(state);
            }
        }
        validate();
        repaint();
    }

    void addState(GameState state) {
        addStateImpl(state);
        validate();
        repaint();
    }

    private void addStateImpl(GameState state) {
        final var view = new StateView(state);
        view.addMouseListener(new ClickHandler(() -> {
            if (onSelect != null) {
                onSelect.accept(state);
            }
        }));
        stack.add(view);
        stack.add(Box.createVerticalStrut(5));
    }

    void clearStates() {
        stack.removeAll();
        validate();
        repaint();
    }
}
