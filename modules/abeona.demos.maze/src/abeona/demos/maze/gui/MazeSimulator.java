package abeona.demos.maze.gui;

import abeona.Query;
import abeona.TerminationType;
import abeona.behaviours.AbstractBehaviour;
import abeona.behaviours.TerminateOnGoalStateBehaviour;
import abeona.demos.maze.*;
import abeona.frontiers.ManagedFrontier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static abeona.demos.maze.gui.MazeProgram.*;
import static abeona.demos.maze.gui.QueryHelpers.isGoalState;

class MazeSimulator extends JFrame implements ActionListener {
    private final Color heapColor = Color.yellow;
    private final Color frontierColor = Color.green;
    private final Color currentColor = Color.red;
    private final Color goalColor = Color.orange;

    private final ImageView mazeView = new ImageView();
    private final JButton resetButton = new JButton("Reset");
    private final JButton nextButton = new JButton("Next step");
    private final JButton nextTenButton = new JButton("Next 10 steps");
    private final JButton runButton = new JButton("Run until termination");
    private final JLabel counterLabel = new JLabel();
    private final JLabel terminationLabel = new JLabel();
    private final Maze maze = createMaze();
    private PlayerState lastEvaluated = null;
    private Query<PlayerState> query = null;
    private int stepCounter = 0;

    MazeSimulator() {
        setPreferredSize(new Dimension(400, 480));
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(mazeView, BorderLayout.CENTER);
        final var buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);
        nextButton.addActionListener(this);
        buttonPanel.add(nextButton);
        nextTenButton.addActionListener(this);
        buttonPanel.add(nextTenButton);
        runButton.addActionListener(this);
        buttonPanel.add(runButton);
        buttonPanel.add(counterLabel);
        buttonPanel.add(terminationLabel);
        add(buttonPanel, BorderLayout.SOUTH);
        onReset();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        final var source = actionEvent.getSource();
        if (source == resetButton) {
            onReset();
        } else if (source == nextButton) {
            onNext(1);
        } else if (source == nextTenButton) {
            onNext(10);
        } else if (source == runButton) {
            onRun();
        }
    }

    private Image renderMaze() {
        final var r = new MazeRenderer(maze);
        final var heap = query.getHeap();
        final var frontier = query.getFrontier();
        final var managed = (frontier instanceof ManagedFrontier) ? (ManagedFrontier<PlayerState>) frontier : null;
        r.paintFloor(cell -> {
            final var ps = new PlayerState(cell);
            if (ps.equals(lastEvaluated)) {
                return Optional.of(currentColor);
            } else if (managed != null && managed.contains(ps)) {
                return Optional.of(frontierColor);
            } else if (heap.contains(ps)) {
                return Optional.of(heapColor);
            } else if (isGoalState(ps)) {
                return Optional.of(goalColor);
            } else {
                return Optional.empty();
            }
        });
        r.paintWalls();
        return r.toImage();
    }

    private void onReset() {
        initQuery();
        lastEvaluated = null;
        stepCounter = 0;
        terminationLabel.setVisible(false);
        setControlsEnabled(true);
        updateView();
    }

    private void initQuery() {
        query = MazeProgram.createQuery(maze);
        query.getFrontier().add(Stream.of(new PlayerState(maze.at(new Position(START_X, START_Y)).orElseThrow())));
        query.addBehaviour(new AbstractBehaviour<>() {
            @Override
            public void attach(Query<PlayerState> query) {
                tapQueryBehaviour(query, query.afterStatePicked, stateEvent -> {
                    MazeSimulator.this.stepCounter++;
                    lastEvaluated = stateEvent.getState();
                });
            }
        });
        //noinspection unchecked
        query.getBehaviours(TerminateOnGoalStateBehaviour.class)
                .findFirst()
                .ifPresent(behaviour -> ((TerminateOnGoalStateBehaviour<PlayerState>) behaviour).onGoal.tap(goalEvent -> {
                    lastEvaluated = goalEvent.getTransition().getTargetState();
                }));
    }

    private void onNext(int amount) {
        final var termination = onNextRunSteps(amount);
        termination.ifPresentOrElse(type -> {
            terminationLabel.setText("Exploration terminated: " + type.toString());
            terminationLabel.setVisible(true);
            if (type != TerminationType.ManualTermination) {
                setControlsEnabled(false);
            }
        }, () -> {
            terminationLabel.setVisible(false);
            setControlsEnabled(true);
        });
        updateView();
    }

    private Optional<TerminationType> onNextRunSteps(int amount) {
        for (int i = 0; i < amount; i++) {
            final var termination = query.exploreNext();
            if (termination.isPresent()) {
                return termination;
            }
        }
        return Optional.empty();
    }

    private void onRun() {
        final var type = query.explore();
        terminationLabel.setText("Exploration terminated: " + type.toString());
        terminationLabel.setVisible(true);
        if (type != TerminationType.ManualTermination) {
            setControlsEnabled(false);
        }
        updateView();
    }

    private void updateView() {
        counterLabel.setText("Steps: " + stepCounter);
        mazeView.setImage(renderMaze());
        repaint();
    }

    private void setControlsEnabled(boolean value) {
        nextButton.setEnabled(value);
        nextTenButton.setEnabled(value);
        runButton.setEnabled(value);
    }

    static Maze createMaze() {
        // Create a maze with the specified size
        return new MazeGenerator(new Random(1)).createMazeSubdiv(MAZE_WIDTH, MAZE_HEIGHT);
    }
}
