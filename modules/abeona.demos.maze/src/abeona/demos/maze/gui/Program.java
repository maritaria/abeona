package abeona.demos.maze.gui;

public final class Program {
    public static void main(String[] args) {
        final var simulator = new MazeSimulator();
        simulator.pack();
        simulator.setVisible(true);
    }
}
