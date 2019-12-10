package abeona.demos.sokoban.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImagePanel extends JPanel {
    private final List<Image> images = new ArrayList<>();

    public ImagePanel() {}

    public ImagePanel(BufferedImage image) {
        images.add(image);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (final var image : images) {
            final var scaledImage = image.getScaledInstance(getWidth(), getHeight(), 0);
            g.drawImage(scaledImage, 0, 0, this);
        }
    }

    public void setImage(Image image) {
        images.clear();
        images.add(image);
    }

    public void addImage(Image image) {
        images.add(image);
        repaint();
    }
}
