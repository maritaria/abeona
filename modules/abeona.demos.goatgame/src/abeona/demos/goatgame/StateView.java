package abeona.demos.goatgame;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

class StateView extends JPanel {
    private static final Color COLOR_TEXT = new Color(0xFFFFFF);
    private static final Color COLOR_DISABLED = new Color(0xBDBDBD);
    private static final Color COLOR_LAND = new Color(0x388E3C);
    private static final Color COLOR_WATER = new Color(0x1976D2);
    private static final Color COLOR_ALERT = new Color(0xD32F2F);

    private final JPanel leftPanel = new JPanel();
    private final JPanel waterPanel = new JPanel();
    private final JPanel rightPanel = new JPanel();
    private final JLabel boatLabel = new JLabel("Boat");
    private final JLabel seedsLabel = new JLabel("Seeds");
    private final JLabel goatLabel = new JLabel("Goat");
    private final JLabel wolfLabel = new JLabel("Wolf");
    private final JLabel invalidLabel = new JLabel("Invalid");

    private GameState currentState;
    boolean allowClicking = false;

    StateView(GameState state) {
        // Layout
        setPreferredSize(new Dimension(240, 100));
        setMaximumSize(getPreferredSize());
        setLayout(new GridLayout(1, 3));
        // Panels
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(COLOR_LAND);
        add(leftPanel);
        waterPanel.setLayout(new BoxLayout(waterPanel, BoxLayout.Y_AXIS));
        waterPanel.setBackground(COLOR_WATER);
        add(waterPanel);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(COLOR_LAND);
        add(rightPanel);
        // Labels
        final var font = new Font("roboto", Font.BOLD, 18);
        for (final var label : Arrays.asList(boatLabel, seedsLabel, goatLabel, wolfLabel, invalidLabel)) {
            label.setFont(font);
            label.setForeground(COLOR_TEXT);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        invalidLabel.setBackground(COLOR_ALERT);
        boatLabel.addMouseListener(new ClickHandler(this::swapBoat));
        seedsLabel.addMouseListener(new ClickHandler(this::swapSeeds));
        goatLabel.addMouseListener(new ClickHandler(this::swapGoat));
        wolfLabel.addMouseListener(new ClickHandler(this::swapWolf));
        invalidLabel.addMouseListener(new ClickHandler(() -> this.setGameState(null)));
        // State
        setGameState(state);
    }

    GameState getGameState() {
        return this.currentState;
    }

    void setGameState(GameState state) {
        state = (state != null) ? state : new GameState();
        this.currentState = state;
        leftPanel.removeAll();
        waterPanel.removeAll();
        rightPanel.removeAll();
        (state.boatIsLeft ? leftPanel : rightPanel).add(boatLabel);
        (state.seedsIsLeft ? leftPanel : rightPanel).add(seedsLabel);
        (state.goatIsLeft ? leftPanel : rightPanel).add(goatLabel);
        (state.wolfIsLeft ? leftPanel : rightPanel).add(wolfLabel);
        if (state.isValid()) {
            waterPanel.remove(invalidLabel);
            waterPanel.setBackground(COLOR_WATER);
        } else {
            waterPanel.add(invalidLabel);
            waterPanel.setBackground(COLOR_ALERT);
        }
        seedsLabel.setForeground(state.boatIsLeft == state.seedsIsLeft ? COLOR_TEXT : COLOR_DISABLED);
        goatLabel.setForeground(state.boatIsLeft == state.goatIsLeft ? COLOR_TEXT : COLOR_DISABLED);
        wolfLabel.setForeground(state.boatIsLeft == state.wolfIsLeft ? COLOR_TEXT : COLOR_DISABLED);
        validate();
        repaint();
    }

    void swapBoat() {
        if (allowClicking) {
            final var next = new GameState(currentState);
            next.boatIsLeft = !next.boatIsLeft;
            setGameState(next);
        }
    }

    void swapSeeds() {
        if (allowClicking) {
            final var next = new GameState(currentState);
            if (next.boatIsLeft == next.seedsIsLeft) {
                next.boatIsLeft = !next.boatIsLeft;
                next.seedsIsLeft = next.boatIsLeft;
                setGameState(next);
            }
        }
    }

    void swapGoat() {
        if (allowClicking) {
            final var next = new GameState(currentState);
            if (next.boatIsLeft == next.goatIsLeft) {
                next.boatIsLeft = !next.boatIsLeft;
                next.goatIsLeft = next.boatIsLeft;
                setGameState(next);
            }
        }
    }

    void swapWolf() {
        if (allowClicking) {
            final var next = new GameState(currentState);
            if (next.boatIsLeft == next.wolfIsLeft) {
                next.boatIsLeft = !next.boatIsLeft;
                next.wolfIsLeft = next.boatIsLeft;
                setGameState(next);
            }
        }
    }


}
