import abeona.ExplorationQuery;
import abeona.Transition;
import abeona.util.ExplorationPresets;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {
    Network createNetwork() {

        final var network = new Network();
        final var start = network.addStation("start");
        start
                .createNeighbour("left-1", 1) // 1
                .createNeighbour("left-2", 2) // 3
                .createNeighbour("left-3", 1); // 4
        start
                .createNeighbour("right-1", 2) // 2
                .createNeighbour("right-2", 3) // 5
                .createNeighbour("right-3", 1); // 6
        final var end = network.addStation("end");
        network.getStation("left-3").orElseThrow().linkTo(end, 3); // 9=7
        network.getStation("right-3").orElseThrow().linkTo(end, 3); // 11
        return network;
    }

    @Test
    void testDijkstraOrder() {
        final var query = ExplorationPresets.setupDijkstra(Network.Station::next, stationTransition -> (double) (int) stationTransition.getUserdata());
        final var network = createNetwork();
        query.getFrontier().add(Stream.of(network.getStation("start").orElseThrow()));
        final var evaluationOrder = new ArrayList<Network.Station>();
        query.afterStatePicked.tap(event -> evaluationOrder.add(event.getState()));
        query.explore();

        final var expectedOrder = List.of(
                network.getStation("start").orElseThrow(),
                network.getStation("left-1").orElseThrow(),
                network.getStation("right-1").orElseThrow(),
                network.getStation("left-2").orElseThrow(),
                network.getStation("left-3").orElseThrow(),
                network.getStation("right-2").orElseThrow(),
                network.getStation("right-3").orElseThrow(),
                network.getStation("end").orElseThrow()
        );
        assertIterableEquals(expectedOrder, evaluationOrder);
    }
}