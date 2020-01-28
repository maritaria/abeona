package abeona.demos.sokoban.gui;

import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.SweepLineBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.demos.sokoban.PlayerMoveActions;
import abeona.demos.sokoban.SokobanState;
import abeona.demos.sokoban.gui.levels.LevelReader;
import abeona.frontiers.QueueFrontier;
import abeona.frontiers.TreeMapFrontier;
import abeona.heaps.HashSetHeap;

import javax.swing.*;
import java.util.Comparator;

public class SokobanProgram {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        final var simulator = new SokobanSimulator();
        simulator.pack();
        simulator.setVisible(true);
    }

    static Query<SokobanState> createQuery(SokobanState initialState) {
        // Frontier
        final var frontier = TreeMapFrontier.<SokobanState>withCollisions(
                Comparator.comparing(s -> s.getBoxes().stream().filter(b -> s.getLevel().isButton(b)).count()),
                s -> s.hashCode()
        );

        // Heap
        final var heap = new HashSetHeap<SokobanState>();

        // Next function
        final NextFunction<SokobanState> nextFunction = PlayerMoveActions::nextStates;

        // Setup query
        final var query = new Query<SokobanState>(frontier, heap, nextFunction);

        // Add behaviours
        query.addBehaviour(new TerminateOnGoalStateBehaviour<>(SokobanState::isSolved));
        //query.addBehaviour(new SweepLineBehaviour<>(Comparator.comparing(s -> s.getBoxes().stream().filter(b -> s.getLevel().isButton(b)).count())));

        return query;
    }

    static SokobanState createInitialState() {
        return LevelReader.readLevel("level-5.txt");
    }
}
