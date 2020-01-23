
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class RobotState {

    private final Set<Integer> deltas;

    private int level, maxLevel;

    private RobotState(int level, int maxLevel, Set<Integer> deltas) {
        this.level = level;
        this.maxLevel = maxLevel;
        this.deltas = deltas;
    }

    public RobotState(int maxLevel, Set<Integer> deltas) {
        this(0, maxLevel, deltas);
    }

    public boolean isGoal() {
        return level == maxLevel;
    }

    public RobotState step(int delta) {
        return new RobotState(level + delta, maxLevel, deltas);
    }

    public Stream<RobotState> next() {
        return deltas.stream()
                .map(delta -> delta + level)
                .filter(newLevel -> 0 <= newLevel && newLevel <= maxLevel)
                .map(newLevel -> new RobotState(newLevel, maxLevel, deltas));
    }

    public int getLevel() {
        return level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, maxLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof RobotState)) return false;

        RobotState that = (RobotState) o;
        return this.level == that.level && this.maxLevel == that.maxLevel;
    }

    @Override
    public String toString() {
        return "RobotState{" + level + ", " + maxLevel + "}";
    }
}
