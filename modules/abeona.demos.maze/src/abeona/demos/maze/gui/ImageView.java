package abeona.demos.maze.gui;

import javax.swing.*;
import java.awt.*;

class ImageView extends JPanel {
    private Image image = null;

    ImageView() {
    }

    public void setImage(Image img) {
        this.image = img;
        this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
        this.validate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}
