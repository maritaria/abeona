package abeona.demos.maze.gui;

import abeona.demos.maze.Maze;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MazeView extends JPanel {
    private Maze maze;

    public MazeView() {
        maze = new Maze(1, 1);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }
        });
    }
}
