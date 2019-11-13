package abeona.demos.maze.gui;

import abeona.Query;
import abeona.demos.maze.PlayerState;

import javax.swing.*;

public class MazeSimulator extends JFrame {
    private final MazeView mazeView = new MazeView();
    private Query<PlayerState> query;

    MazeSimulator() {
    }
}
