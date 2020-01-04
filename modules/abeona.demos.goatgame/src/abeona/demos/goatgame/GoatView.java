package abeona.demos.goatgame;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GoatView extends JPanel {
    private static final Color COLOR_TEXT = new Color(0xFFFFFF);
    private static final Color COLOR_LAND = new Color(0x388E3C);
    private static final Color COLOR_WATER = new Color(0x1976D2);
    private final JPanel leftPanel = new JPanel();
    private final JPanel waterPanel = new JPanel();
    private final JPanel rightPanel = new JPanel();
    private final JLabel boatLabel = new JLabel("Boat");
    private final JLabel seedsLabel = new JLabel("Seeds");
    private final JLabel goatLabel = new JLabel("Goat");
    private final JLabel wolfLabel = new JLabel("Wolf");
    private final GameState state;

    public GoatView(GameState state) {
        this.state = state;
        final var font = new Font("roboto", Font.BOLD, 18);
        for (final var label : Arrays.asList(boatLabel, seedsLabel, goatLabel, wolfLabel)) {
            label.setFont(font);
            label.setForeground(COLOR_TEXT);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
        }


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

        if (state.boatIsLeft) {
            leftPanel.add(boatLabel);
            rightPanel.add(Box.createRigidArea(boatLabel.getPreferredSize()));
        } else {
            leftPanel.add(Box.createRigidArea(boatLabel.getPreferredSize()));
            rightPanel.add(boatLabel);
        }

        if (state.seedsIsLeft) {
            leftPanel.add(seedsLabel);
            rightPanel.add(Box.createRigidArea(seedsLabel.getPreferredSize()));
        } else {
            leftPanel.add(Box.createRigidArea(seedsLabel.getPreferredSize()));
            rightPanel.add(seedsLabel);
        }

        if (state.goatIsLeft) {
            leftPanel.add(goatLabel);
            rightPanel.add(Box.createRigidArea(goatLabel.getPreferredSize()));
        } else {
            leftPanel.add(Box.createRigidArea(goatLabel.getPreferredSize()));
            rightPanel.add(goatLabel);
        }

        if (state.wolfIsLeft) {
            leftPanel.add(wolfLabel);
            rightPanel.add(Box.createRigidArea(wolfLabel.getPreferredSize()));
        } else {
            leftPanel.add(Box.createRigidArea(wolfLabel.getPreferredSize()));
            rightPanel.add(wolfLabel);
        }


    }
}
