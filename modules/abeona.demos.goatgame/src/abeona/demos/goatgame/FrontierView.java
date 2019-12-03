package abeona.demos.goatgame;

import abeona.frontiers.ManagedFrontier;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class FrontierView extends JPanel {
    Consumer<GameState> onSelect;

    FrontierView() {
        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(300, 400));
    }

    void setFrontier(ManagedFrontier<GameState> frontier) {
        removeAll();
        if (frontier != null) {
            for (GameState state : frontier) {
                final var view = new GameView(state);
                view.addMouseListener(new ClickHandler(() -> {
                    if (onSelect != null) {
                        onSelect.accept(state);
                    }
                }));
                add(view);
            }
        }
    }
}
