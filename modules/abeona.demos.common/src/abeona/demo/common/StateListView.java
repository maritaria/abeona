package abeona.demo.common;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.function.Function;

public class StateListView<StateType> extends JPanel {
    private final JPanel stack = new JPanel();
    private final JScrollPane scroll;
    private final Function<StateType, JPanel> viewCreator;
    private final HashMap<StateType, JPanel> createdViews = new HashMap<>();

    StateListView(String title, Function<StateType, JPanel> viewCreator) {
        this.viewCreator = viewCreator;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        if (title != null) {
            setBorder(BorderFactory.createTitledBorder(title));
        }
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        scroll = new JScrollPane(stack);
        scroll.setPreferredSize(new Dimension(200, 500));
        add(scroll);
    }

    void setStates(Iterable<StateType> frontier) {
        stack.removeAll();
        if (frontier != null) {
            for (StateType state : frontier) {
                addStateImpl(state);
            }
        }
        updateUI();
    }

    void addState(StateType state) {
        addStateImpl(state);
        updateUI();
    }

    void highlightState(StateType state, Color color) {
        final var view = createdViews.get(state);
        if (view != null) {
            view.setBorder(BorderFactory.createLineBorder(color, 2));
        }
    }

    private void addStateImpl(StateType state) {
        final var view = viewCreator.apply(state);
        createdViews.put(state, view);
        stack.add(view);
        stack.add(Box.createRigidArea(new Dimension(5, 5)));
    }

    void clearStates() {
        stack.removeAll();
        updateUI();
    }
}
