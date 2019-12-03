package abeona.demos.goatgame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ClickHandler implements MouseListener {
    final Runnable handler;

    ClickHandler(Runnable handler) {
        this.handler = handler;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        handler.run();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {}

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {}

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {}

    @Override
    public void mouseExited(MouseEvent mouseEvent) {}

}
