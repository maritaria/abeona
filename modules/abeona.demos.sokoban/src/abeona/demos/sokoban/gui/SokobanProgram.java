package abeona.demos.sokoban.gui;

import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.SweepLineBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.demos.sokoban.PlayerMoveActions;
import abeona.demos.sokoban.Position;
import abeona.demos.sokoban.SokobanLevel;
import abeona.demos.sokoban.SokobanState;
import abeona.frontiers.QueueFrontier;
import abeona.frontiers.TreeMapFrontier;
import abeona.heaps.HashSetHeap;

import java.util.Comparator;
import java.util.HashSet;

public class SokobanProgram {
    public static void main(String[] args) {
        final var simulator = new SokobanSimulator();
        simulator.pack();
        simulator.setVisible(true);
    }

    public static Query<SokobanState> createQuery(SokobanState initialState) {
        final var frontier = QueueFrontier.<SokobanState>fifoFrontier();
        // final var frontier = TreeMapFrontier.<SokobanState>withExactOrdering(progressComparatorWithoutCollisions(initialState));
        final var heap = new HashSetHeap<SokobanState>();
        final NextFunction<SokobanState> nextFunction = PlayerMoveActions::nextStates;
        final var query = new Query<SokobanState>(frontier, heap, nextFunction);
        query.addBehaviour(new TerminateOnGoalStateBehaviour<>(SokobanState::isSolved));

        query.addBehaviour(new SweepLineBehaviour<SokobanState>(progressComparator(initialState)));

        return query;
    }

    private static Comparator<SokobanState> progressComparator(SokobanState initialState) {
        final var level = initialState.getLevel();
        return Comparator.comparingInt(state -> (int) state.getBoxes().stream().filter(level::isButton).count());
    }

    private static Comparator<SokobanState> progressComparatorWithoutCollisions(SokobanState initialState) {
        return progressComparator(initialState).thenComparing(SokobanState.nonCollidingComparator());
    }


    public static SokobanState createInitialState() {
        // https://www.researchgate.net/profile/Bilal_Kartal/publication/312538695/figure/fig2/AS:669563205197838@1536647716634/A-solution-to-one-of-the-generated-Sokoban-puzzles-score-017-Each-successive-frame.png
        final var level = new SokobanLevel(5, 5);
        level.setWall(2, 0, true);
        level.setWall(3, 0, true);
        level.setWall(4, 0, true);
        level.setWall(4, 1, true);
        level.setWall(4, 2, true);
        level.setWall(3, 2, true);
        level.setWall(1, 4, true);
        level.setWall(2, 4, true);
        level.setWall(3, 4, true);
        level.addButton(new Position(0, 3));
        level.addButton(new Position(0, 4));
        level.addButton(new Position(3, 3));
        final var boxes = new HashSet<Position>();
        boxes.add(new Position(1, 1));
        boxes.add(new Position(1, 2));
        boxes.add(new Position(1, 3));
        final var start = new Position(2, 2);
        return new SokobanState(level, boxes, start);
    }
}
