package abeona.demos.salesman.gui;

import abeona.NextFunction;
import abeona.Query;
import abeona.behaviours.SimulatedAnnealingBehaviour;
import abeona.demos.salesman.City;
import abeona.demos.salesman.SalesmanPath;
import abeona.frontiers.ManagedFrontier;
import abeona.frontiers.QueueFrontier;
import abeona.heaps.NullHeap;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

public class Simulator extends JFrame {
    private final PathView pathView = new PathView();
    private final JLabel terminationLabel = new JLabel();
    private SalesmanPath origin;
    private final Query<SalesmanPath> query;
    private final SimulatedAnnealingBehaviour<SalesmanPath> annealing;


    public Simulator() {
        // Init query
        query = new Query<>(QueueFrontier.fifoFrontier(),
                new NullHeap<>(),
                NextFunction.wrap(SalesmanPath::nextAnnealing));
        annealing = new SimulatedAnnealingBehaviour<>(100,
                0.2,
                1,
                SalesmanPath::getLength,
                SimulatedAnnealingBehaviour.TransitionChance.standard());
        annealing.attach(query);

        // Init visuals
        setPreferredSize(new Dimension(400, 300));
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());
        // Setup content
        final var content = getContentPane();
        // Component: PathView
        content.add(pathView, BorderLayout.CENTER);
        // Component: Buttons panel
        final var buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        content.add(buttonsPanel, BorderLayout.SOUTH);
        // Button: new cities
        final var newCitiesButton = new JButton("New cities");
        newCitiesButton.addActionListener(event -> this.changeOrigin());
        buttonsPanel.add(newCitiesButton);
        // Button: step
        final var nextStepButton = new JButton("Next step");
        nextStepButton.addActionListener(event -> this.nextStep(1));
        buttonsPanel.add(nextStepButton);
        // Button: step x10
        final var fastStepButton = new JButton("10 steps");
        fastStepButton.addActionListener(event -> this.nextStep(10));
        buttonsPanel.add(fastStepButton);
        // Button: step x100
        final var megaStepButton = new JButton("100 steps");
        megaStepButton.addActionListener(event -> this.nextStep(10000));
        buttonsPanel.add(megaStepButton);
        // Button: Reset temp
        final var resetTemperatureButton = new JButton("Reset temperature");
        resetTemperatureButton.addActionListener(event -> this.annealing.resetTemperature(this.query));
        buttonsPanel.add(resetTemperatureButton);
        content.add(terminationLabel, BorderLayout.NORTH);
        terminationLabel.setVisible(false);
    }

    private void changeOrigin() {
        origin = createOrigin();
        pathView.setPath(origin);
        query.getFrontier().clear();
        query.getHeap().clear();
        query.getFrontier().add(Stream.of(origin));
        annealing.resetTemperature(query);
    }

    private SalesmanPath createOrigin() {
        final int width = pathView.getWidth();
        final int height = pathView.getHeight();
        final long cityCount = 2 + Math.round(Math.sqrt(width + height));
        Random r = new Random(System.currentTimeMillis());
        ArrayList<City> cities = new ArrayList<>();
        for (int i = 0; i < cityCount; i++) {
            final var city = new City("" + i, r.nextInt(width), r.nextInt(height));
            cities.add(city);
        }
        return new SalesmanPath(cities);
    }

    private void nextStep(int amount) {
        terminationLabel.setVisible(false);
        for (int i = 0; i < amount; i++) {
            final var termination = query.exploreNext();
            if (termination.isPresent()) {
                terminationLabel.setText("Exploration terminated: " + termination.get().toString());
                terminationLabel.setVisible(true);
                break;
            }
        }
        final var frontier = (ManagedFrontier<SalesmanPath>) query.getFrontier();
        final var first = frontier.stream().findFirst();
        first.ifPresent(pathView::setPath);
        pathView.setAnnealing("T: " + annealing.getTemperature(query).orElse(-123));
    }
}
