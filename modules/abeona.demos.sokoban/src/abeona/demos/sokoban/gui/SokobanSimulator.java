package abeona.demos.sokoban.gui;

import abeona.Query;
import abeona.TerminationType;
import abeona.behaviours.AbstractBehaviour;
import abeona.demos.sokoban.Position;
import abeona.demos.sokoban.SokobanState;
import abeona.frontiers.ManagedFrontier;
import abeona.heaps.ManagedHeap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static abeona.demos.sokoban.gui.SokobanProgram.createInitialState;
import static abeona.demos.sokoban.gui.SokobanProgram.createQuery;

public class SokobanSimulator extends JFrame implements ActionListener, ItemListener {
    private final StepPanel stepPanel = new StepPanel();
    private final GraphView frontierGraph = new GraphView();
    private final GraphView heapGraph = new GraphView();
    private final JButton resetButton = new JButton("Reset");
    private final JButton nextButton = new JButton("Next step");
    private final JButton nextTenButton = new JButton("Next 10 steps");
    private final JButton runButton = new JButton("Run to termination");
    private final JLabel stepLabel = new JLabel();
    private final JLabel estimateLabel = new JLabel();
    private final JLabel terminationLabel = new JLabel();
    private final JCheckBox showFrontiersCheckox = new JCheckBox("Show frontiers", true);

    private SokobanState initialState;
    private Query<SokobanState> query;
    private long stepCounter = 0;

    SokobanSimulator() {
        setPreferredSize(new Dimension(400, 480));
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        final var content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(stepPanel);
        final var buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        resetButton.addActionListener(this);
        buttonBar.add(resetButton);
        nextButton.addActionListener(this);
        buttonBar.add(nextButton);
        nextTenButton.addActionListener(this);
        buttonBar.add(nextTenButton);
        runButton.addActionListener(this);
        buttonBar.add(runButton);
        showFrontiersCheckox.addItemListener(this);
        buttonBar.add(showFrontiersCheckox);
        buttonBar.add(stepLabel);
        buttonBar.add(estimateLabel);
        buttonBar.add(terminationLabel);
        content.add(buttonBar);
        frontierGraph.setBorder(BorderFactory.createTitledBorder("Frontier size"));
        frontierGraph.setValues(Collections.emptyList());
        content.add(frontierGraph);
        heapGraph.setBorder(BorderFactory.createTitledBorder("Heap size"));
        heapGraph.setValues(Collections.emptyList());
        content.add(heapGraph);
        add(new JScrollPane(content), BorderLayout.CENTER);
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

    void onReset() {
        initialState = createInitialState();
        query = createQuery(initialState);
        query.getFrontier().add(Stream.of(initialState));
        query.addBehaviour(new AbstractBehaviour<>() {
            @Override
            public void attach(Query<SokobanState> query) {
                tapQueryBehaviour(query, query.afterStateEvaluation, evaluationEvent -> {
                    stepCounter++;
                });
                final var frontier = query.getFrontier();
                if (frontier instanceof ManagedFrontier) {
                    final var managed = (ManagedFrontier<SokobanState>) frontier;
                    tapQueryBehaviour(query, query.afterStateEvaluation, evaluationEvent -> {
                        frontierGraph.addValues(Collections.singleton((int) managed.size()));
                    });
                }
                final var heap = query.getHeap();
                if (heap instanceof ManagedHeap) {
                    final var managed = (ManagedHeap<SokobanState>) heap;
                    tapQueryBehaviour(query, query.afterStateEvaluation, evaluationEvent -> {
                        heapGraph.addValues(Collections.singleton((int) managed.size()));
                    });
                }
            }
        });
        frontierGraph.setValues(Collections.emptyList());
        heapGraph.setValues(Collections.emptyList());
        stepPanel.setQuery(query);
        updateEstimateSize();
        nextButton.setEnabled(true);
        nextTenButton.setEnabled(true);
        runButton.setEnabled(true);
        stepCounter = 0;
        terminationLabel.setVisible(false);
        updateViews();
    }

    private void updateEstimateSize() {
        final var level = initialState.getLevel();
        final var width = level.getWidth();
        final var height = level.getHeight();
        final var openSpots = IntStream.range(0, width)
                .mapToObj(x -> IntStream.range(0, height).mapToObj(y -> new Position(x, y)))
                .flatMap(Function.identity())
                .filter(level::isWall)
                .count();
        final var boxes = initialState.getBoxes().size();
        final var movableEntities = boxes + 1;
        var mutations = 1;
        for (int i = 0; i < movableEntities; i++) {
            mutations *= (openSpots - i);
        }
        estimateLabel.setText("Estimate state space size: " + mutations);
    }

    void onNext(int steps) {
        final var termination = onNextRunSteps(steps);
        termination.ifPresent(this::showTermination);
        updateViews();
    }

    private void updateViews() {
        stepLabel.setText("Steps: " + stepCounter);
        validate();
        repaint();
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

    void onRun() {
        final var termination = query.explore();
        showTermination(termination);
        updateViews();
    }

    private void showTermination(TerminationType termination) {
        terminationLabel.setText("Termination: " + termination);
        terminationLabel.setVisible(true);
        if (termination == TerminationType.FrontierExhaustion) {
            nextButton.setEnabled(false);
            nextTenButton.setEnabled(false);
            runButton.setEnabled(false);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        final var source = itemEvent.getSource();
        if (source == showFrontiersCheckox) {
            onShowFrontiersChanged(itemEvent);
        }
    }

    private void onShowFrontiersChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            stepPanel.setFrontiersEnabled(true);
        } else {
            stepPanel.setFrontiersEnabled(false);

        }
    }
}
