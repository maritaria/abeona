package abeona.demos.sokoban.gui;

import abeona.demos.sokoban.SokobanState;

import javax.swing.*;
import java.awt.*;

class StateListView extends JPanel {
    private final JPanel stack = new JPanel();
    private final JScrollPane scroll;
    private int items = 0;

    StateListView(String title) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        if (title != null) {
            setBorder(BorderFactory.createTitledBorder(title));
        }
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        scroll = new JScrollPane(stack);
        scroll.setPreferredSize(new Dimension(300, 500));
        add(scroll);
    }

    void setStates(Iterable<SokobanState> frontier) {
        stack.removeAll();
        items = 0;
        if (frontier != null) {
            for (SokobanState state : frontier) {
                addStateImpl(state);
            }
        }
        updateUI();
    }

    void addState(SokobanState state) {
        addStateImpl(state);
        updateUI();
    }

    private void addStateImpl(SokobanState state) {
        final var view = new SokobanViewer();
        view.showSokoban(state);
        stack.add(view);
        items++;
    }

    void clearStates() {
        stack.removeAll();
        items = 0;
        updateUI();
    }
}
