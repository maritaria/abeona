import java.awt.*;
import java.util.Objects;

public class RobotState {
    public RobotState(int maxHeight, int stepHeight) {
        this.maxHeight = maxHeight;
        this.stepHeight = stepHeight;
    }

    private int maxHeight;
    private int stepHeight;

    public int getStepHeight() {
        return stepHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public boolean isCrashed() {
        return stepHeight >= maxHeight || stepHeight < 0;
    }

    public RobotState climb() {
        return new RobotState(this.maxHeight, this.stepHeight + 5);
    }

    public RobotState descend() {
        return new RobotState(this.maxHeight, this.stepHeight - 3);
    }

    public boolean isFinished() {
        return this.maxHeight == stepHeight;
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
        return maxHeight == that.maxHeight && stepHeight == that.stepHeight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxHeight, stepHeight);
    }

    @Override
    public String toString() {
        return "RobotState{" + "maxHeight=" + maxHeight + ", stepHeight=" + stepHeight + '}';
    }
}
