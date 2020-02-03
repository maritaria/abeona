import abeona.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class RobotState {
    public int getRobotState() {
        return robotState;
    }

    private final int robotState;
    private final int stairHeight;

    public RobotState(int robotState, int stairHeight) {
        this.robotState = robotState;
        this.stairHeight = stairHeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RobotState that = (RobotState) o;
        return robotState == that.robotState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(robotState);
    }

    public Stream<RobotState> next() {
        List<RobotState> results = new ArrayList<>();

        // kijken of robot omhoog kan -> dan omhoog
        if(this.robotState+ 7 <= this.stairHeight) {
            results.add(new RobotState(this.robotState + 7, this.stairHeight));
        }

        if(this.robotState+ 5 <= this.stairHeight) {
            results.add(new RobotState(this.robotState + 5, this.stairHeight));
        }

        if(this.robotState >= 3) {
            results.add(new RobotState(this.robotState - 3, this.stairHeight));
        }
        // anders omlaag
        return results.stream();
    }

    @Override
    public String toString() {
        return "RobotState{" + "robotState=" + robotState + ", stairHeight=" + stairHeight + '}';
    }
}
