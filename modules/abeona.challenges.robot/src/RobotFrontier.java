import abeona.frontiers.Frontier;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class RobotFrontier implements Frontier<RobotState> {

    private RobotState max;

    public RobotFrontier(RobotState initial) {
        this.max = initial;
    }

    @Override
    public boolean add(Stream<? extends RobotState> newDiscoveries) {
        Optional<? extends RobotState> max = newDiscoveries.max(Comparator.comparing(RobotState::getLevel));
        if (!max.isPresent()) {
            this.max = null;
            return this.max == null;
        } else {
            RobotState newMax = max.get();
            boolean result = !newMax.equals(this.max);
            this.max = newMax;
            return result;
        }
    }

    @Override
    public void clear() {
        max = null;
    }

    @Override
    public boolean hasNext() {
        return max != null;
    }

    @Override
    public RobotState next() {
        return max;
    }
}
