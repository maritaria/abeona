package abeona.demos.sokoban;

import java.util.Comparator;

public class Helpers {
    static Position leftOf(Position pos) {
        return new Position(pos.getX() - 1, pos.getY());
    }

    static Position rightOf(Position pos) {
        return new Position(pos.getX() + 1, pos.getY());
    }

    static Position above(Position pos) {
        return new Position(pos.getX(), pos.getY() - 1);
    }

    static Position below(Position pos) {
        return new Position(pos.getX(), pos.getY() + 1);
    }

    /**
     * Tests if a given state is already known to be unsolvable,
     * The test is based on the number of boxes put in corners
     *
     * @param state The state to test
     * @return True if we already know the state to not lead to a solution
     */
    public static boolean isSolvable(SokobanState state) {
        final var boxCount = state.getBoxes().size();
        final var buttonCount = state.getLevel().getButtons().count();
        if (boxCount < buttonCount) {
            // Not enough boxes to put on the buttons
            return false;
        }
        // Get the number of boxes that do not need to be on buttons
        final var freeBoxes = boxCount - buttonCount;
        // If that number is greater than the number of boxes stuck in corners (while not being on a button)
        // Then the state is not solvable anyway because we cannot get it out of a corner
        return numBlockedBoxes(state) <= freeBoxes;
    }

    public static long numBlockedBoxes(SokobanState state) {
        final var level = state.getLevel();
        return state.getBoxes()
                .stream()
                .filter(pos -> !level.isButton(pos))
                .filter(pos -> isWallCorner(level, pos))
                .count();
    }

    public static boolean isWallCorner(SokobanLevel level, Position pos) {
        final boolean wallUp = level.isWall(above(pos));
        final boolean wallDown = level.isWall(below(pos));
        final boolean wallLeft = level.isWall(leftOf(pos));
        final boolean wallRight = level.isWall(rightOf(pos));
        return (wallUp || wallDown) && (wallLeft || wallRight);
    }

    public static Comparator<SokobanState> progressComparator(SokobanState initialState) {
        final var level = initialState.getLevel();
        return Comparator.comparingInt(state -> (int) state.getBoxes().stream().filter(level::isButton).count());
    }

    public static Comparator<SokobanState> progressComparatorWithoutCollisions(SokobanState initialState) {
        return progressComparator(initialState).thenComparing(SokobanState.nonCollidingComparator());
    }

    public static boolean advancedBlockingCheck(SokobanState state) {
        final var level = state.getLevel();
        return state.getBoxes().stream().anyMatch(box -> {
            final var above = above(box);
            final var below = below(box);
            final var leftOf = leftOf(box);
            final var rightOf = rightOf(box);
            final var topLeft = leftOf(above);
            final var topRight = rightOf(above);
            final var bottomLeft = leftOf(below);
            final var bottomRight = rightOf(below);
            if (level.isWall(leftOf) || level.isWall(rightOf)) {
                if (level.isWall(above) || level.isWall(below)) {
                    return true;
                }
                if (state.isBox(above)) {
                    if (level.isWall(topLeft) || level.isWall(topRight)) {
                        return true;
                    }
                }
                if (state.isBox(below)) {
                    if (level.isWall(bottomLeft) || level.isWall(bottomRight)) {
                        return true;
                    }
                }
            }
            if (level.isWall(above) || level.isWall(below)) {
                if (level.isWall(leftOf) || level.isWall(rightOf)) {
                    return true;
                }
                if (state.isBox(leftOf)) {
                    if (level.isWall(topLeft) || level.isWall(bottomLeft)) {
                        return true;
                    }
                }
                if (state.isBox(rightOf)) {
                    if (level.isWall(topRight) || level.isWall(bottomRight)) {
                        return true;
                    }
                }
            }
            return false;
        });
    }
}
