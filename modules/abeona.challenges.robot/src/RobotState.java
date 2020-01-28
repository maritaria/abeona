import abeona.Transition;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class RobotState {
    public int height = 0;
    public final static int MAX_HEIGHT = 1000;

    public RobotState(int height) {
        this.height = height;
    }


    public Stream<Transition<RobotState>> next() {
        List<Transition<RobotState>> transitions = new LinkedList<>();
        if (height <= MAX_HEIGHT - 5) {
            transitions.add(new Transition<RobotState>(this, new RobotState(height + 5)));
        }
        if (height <= MAX_HEIGHT - 7) {
            transitions.add(new Transition<RobotState>(this, new RobotState(height + 7)));
        }
        if (height >= 3) {
            transitions.add(new Transition<RobotState>(this, new RobotState(height - 3)));
        }
        return transitions.stream();
    }

    @Override
    public int hashCode() {
        return height;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RobotState && height == ((RobotState)obj).height;
    }

    @Override
    public String toString() {
        return "robot(" + height + ")";
    }
}
