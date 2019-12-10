package abeona.demos.sokoban;

import java.util.stream.Stream;

public interface GameAction {
    String name();

    boolean canPerform(SokobanState state);

    Stream<SokobanState> resultingStates(SokobanState state);
}
