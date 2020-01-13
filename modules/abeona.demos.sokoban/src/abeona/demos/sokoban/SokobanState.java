package abeona.demos.sokoban;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

public final class SokobanState {
    private final SokobanLevel level;
    private final Position player;
    private final Set<Position> boxes;

    public SokobanState(SokobanLevel level, Set<Position> boxes, Position player) {
        this.level = level;
        this.player = player;
        this.boxes = boxes;
    }

    public static Comparator<SokobanState> nonCollidingComparator() {
        return Comparator.<SokobanState>comparingInt(state -> state.getPlayer()
                .getX()).thenComparingInt(state -> state.getPlayer().getY())
                .thenComparing((a, b) -> {
                    final var items = new ArrayList<>(a.boxes);
                    b.boxes.forEach(items::remove);
                    return items.size();
                });
    }

    public SokobanLevel getLevel() {
        return level;
    }

    public Position getPlayer() {
        return player;
    }

    public Set<Position> getBoxes() {
        return boxes;
    }

    public boolean isOpen(Position pos) {
        return !level.isWall(pos) && !boxes.contains(pos);
    }

    public boolean isBox(Position pos) {
        return boxes.contains(pos);
    }

    public boolean isSolved() {
        return boxes.stream().allMatch(level::isButton);
    }

    @Override
    public int hashCode() {
        var hash = level.hashCode() ^ player.hashCode();
        for (final var box : boxes) {
            hash = (hash >>> 3) ^ box.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SokobanState) {
            final var other = (SokobanState) obj;
            return level.equals(other.level) && player.equals(other.player) && boxes.equals(other.boxes);

        } else {
            return false;
        }
    }
}
