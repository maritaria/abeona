package abeona.demos.sokoban.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Rodrigo
 */
public class GraphView extends JPanel {
    private final static int padding = 25;
    private final static int labelPadding = 25;
    private final static int pointWidth = 4;
    private final static int numberYDivisions = 10;
    private final static Color lineColor = new Color(44, 102, 230, 180);
    private final static Color pointColor = new Color(100, 100, 100, 180);
    private final static Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke graphStroke = new BasicStroke(2f);
    private List<Double> scores = new ArrayList<>();

    public GraphView() {
        setPreferredSize(new Dimension(padding * 2 + 300, padding * 2 + 200));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final var width = getWidth();
        final var height = getHeight();
        final var maxScore = getMaxScore();
        final var minScore = getMinScore();
        final var scoreRange = maxScore - minScore;
        final var length = scores.size();

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(
                padding + labelPadding,
                padding,
                width - (2 * padding) - labelPadding,
                height - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        final FontMetrics fontMetrics = g2.getFontMetrics();
        final int fontHeight = fontMetrics.getHeight();

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            final int x1 = padding + labelPadding;
            final int x2 = pointWidth + padding + labelPadding;
            final int y = height - ((i * (height - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            if (length > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y, width - padding, y);
                g2.setColor(Color.BLACK);
                final int tickValue = (int) (minScore + ((scoreRange * i) / numberYDivisions));
                final String yLabel = tickValue + "";
                final int labelWidth = fontMetrics.stringWidth(yLabel);
                g2.drawString(yLabel, x1 - labelWidth - 5, y + (fontHeight / 2) - 3);
            }
            g2.drawLine(x1, y, x2, y);
        }

        // and for x axis
        if (length > 1) {
            for (int i = 0; i < length; i++) {
                final int x = i * (width - padding * 2 - labelPadding) / (length - 1) + padding + labelPadding;
                final int y1 = height - padding - labelPadding;
                final int y2 = y1 - pointWidth;
                if ((i % ((int) ((length / 20.0)) + 1)) == 0) {
                    g2.setColor(gridColor);
                    g2.drawLine(x, height - padding - labelPadding - 1 - pointWidth, x, padding);
                    g2.setColor(Color.BLACK);
                    final String xLabel = i + "";
                    final int labelWidth = fontMetrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x - labelWidth / 2, y1 + fontHeight + 3);
                }
                g2.drawLine(x, y1, x, y2);
            }
        }

        // create x and y axes 
        g2.drawLine(padding + labelPadding, height - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(
                padding + labelPadding,
                height - padding - labelPadding,
                width - padding,
                height - padding - labelPadding);

        final Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(graphStroke);

        final double xScale = ((double) width - (2 * padding) - labelPadding) / (length - 1);
        final double yScale = ((double) height - 2 * padding - labelPadding) / scoreRange;

        final List<Point> graphPoints = new ArrayList<>(length);
        int previousPointX = -10;
        for (int i = 0; i < length; i++) {
            final int x1 = (int) (i * xScale + padding + labelPadding);
            final int y1 = (int) ((maxScore - scores.get(i)) * yScale + padding);
            if (x1 > (previousPointX + pointWidth)) {
                graphPoints.add(new Point(x1, y1));
                previousPointX = x1;
            }
        }

        for (int i = 0; i < graphPoints.size() - 1; i++) {
            final int x1 = graphPoints.get(i).x;
            final int y1 = graphPoints.get(i).y;
            final int x2 = graphPoints.get(i + 1).x;
            final int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        boolean drawDots = width > (length * pointWidth);
        if (drawDots) {
            g2.setStroke(oldStroke);
            g2.setColor(pointColor);
            for (Point graphPoint : graphPoints) {
                final int x = graphPoint.x - pointWidth / 2;
                final int y = graphPoint.y - pointWidth / 2;
                g2.fillOval(x, y, pointWidth, pointWidth);
            }
        }
    }

    private double getMinScore() {
        double minScore = Double.MAX_VALUE;
        for (Double score : scores) {
            minScore = Math.min(minScore, score);
        }
        return minScore;
    }

    private double getMaxScore() {
        double maxScore = Double.MIN_VALUE;
        for (Double score : scores) {
            maxScore = Math.max(maxScore, score);
        }
        return maxScore;
    }

    public void setScores(Collection<Double> values) {
        scores.clear();
        addScores(values);
        invalidate();
        repaint();
    }

    public void addScores(Collection<Double> values) {
        scores.addAll(values);
    }
}