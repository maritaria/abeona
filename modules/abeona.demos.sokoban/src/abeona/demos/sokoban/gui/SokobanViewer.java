package abeona.demos.sokoban.gui;

import abeona.demos.sokoban.Position;
import abeona.demos.sokoban.SokobanState;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SokobanViewer extends JPanel {
    private final Point imageSize = new Point(32, 32);

    public SokobanViewer() {
        setLayout(null);
    }

    public void showSokoban(SokobanState state) {
        // Clear existing stuff
        removeAll();
        // Setup rendering
        final var level = state.getLevel();
        final var width = level.getWidth();
        final var height = level.getHeight();
        final var images = new ImagePanel[width][height];
        // Hint size
        final var size = new Dimension(width * imageSize.x, height * imageSize.y);
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
        // Draw world
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final var pos = new Position(x, y);
                images[x][y] = addImage(Images.floor, x, y);
                if (level.isWall(pos)) {
                    images[x][y].addImage(Images.wall);
                } else if (level.isButton(pos)) {
                    images[x][y].addImage(Images.button);
                }
            }
        }
        // Draw boxes
        for (final var box : state.getBoxes()) {
            images[box.getX()][box.getY()].addImage(Images.box);
        }
        // Draw player
        final var player = state.getPlayer();
        images[player.getX()][player.getY()].addImage(Images.player);

        validate();
        repaint();
    }

    private ImagePanel addImage(BufferedImage image, int x, int y) {
        final var panel = new ImagePanel(image);
        add(panel);
        panel.setBounds(x * imageSize.x, y * imageSize.y, imageSize.x, imageSize.y);
        return panel;
    }
}
