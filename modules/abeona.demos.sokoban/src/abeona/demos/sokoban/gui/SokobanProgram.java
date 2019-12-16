package abeona.demos.sokoban.gui;

import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.demos.sokoban.PlayerMoveActions;
import abeona.demos.sokoban.SokobanState;
import abeona.demos.sokoban.gui.levels.LevelReader;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.HashSetHeap;

import javax.swing.*;

public class SokobanProgram {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        final var simulator = new SokobanSimulator();
        simulator.pack();
        simulator.setVisible(true);
    }

    static Query<SokobanState> createQuery(SokobanState initialState) {
        // Frontier
        final var frontier = QueueFrontier.<SokobanState>fifoFrontier();

        // Heap
        final var heap = new HashSetHeap<SokobanState>();

        // Next function
        final NextFunction<SokobanState> nextFunction = PlayerMoveActions::nextStates;

        // Setup query
        final var query = new Query<SokobanState>(frontier, heap, nextFunction);

        // Add behaviours
        query.addBehaviour(new TerminateOnGoalStateBehaviour<>(SokobanState::isSolved));

        return query;
    }

    static SokobanState createInitialState() {
        return LevelReader.readLevel("level-5.txt");
    }
}
