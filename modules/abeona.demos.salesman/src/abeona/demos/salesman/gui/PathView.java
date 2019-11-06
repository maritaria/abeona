package abeona.demos.salesman.gui;

import abeona.behaviours.SimulatedAnnealingBehaviour;
import abeona.demos.salesman.City;
import abeona.demos.salesman.SalesmanPath;
import abeona.util.Arguments;

import javax.swing.*;
import java.awt.*;

public class PathView extends JComponent {
    private static final Color cityColor = Color.black;
    private static final Color pathColor = Color.red;
    private static final int cityCircleSize = 5;
    private SalesmanPath path;
    private String annealing;

    PathView() {
        setPath(null);
    }

    void setPath(SalesmanPath path) {
        this.path = path;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (path == null) {
            return;
        }
        final var iterator = path.cities().iterator();
        if (!iterator.hasNext()) {
            return;
        }
        var previous = iterator.next();
        this.paintCity(g, previous);

        while (iterator.hasNext()) {
            var next = iterator.next();
            this.paintCity(g, next);
            this.paintPath(g, previous, next);
            previous = next;
        }

        final var length = path.getLength();
        g.setColor(Color.black);
        g.drawString("Length: " + length, 100, 50);
        if (annealing != null) {
            g.drawString("Annealing: " + annealing, 100, 60);
        }
    }

    private void paintCity(Graphics g, City city) {
        Arguments.requireNonNull(city, "city");
        g.setColor(cityColor);
        final int x = city.getPos().getX();
        final int y = city.getPos().getY();
        final int offset = (cityCircleSize / 2);
        g.drawOval(x - offset, y - offset, cityCircleSize, cityCircleSize);
    }

    private void paintPath(Graphics g, City a, City b) {
        Arguments.requireNonNull(a, "a");
        Arguments.requireNonNull(b, "b");
        g.setColor(pathColor);
        g.drawLine(a.getPos().getX(), a.getPos().getY(), b.getPos().getX(), b.getPos().getY());
    }

    public void setAnnealing(String annealing) {
        this.annealing = annealing;
        this.repaint();
    }
}
