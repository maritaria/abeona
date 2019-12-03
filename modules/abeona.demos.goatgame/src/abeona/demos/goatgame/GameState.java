package abeona.demos.goatgame;

import abeona.State;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class GameState implements State {
    boolean boatIsLeft, seedsIsLeft, goatIsLeft, wolfIsLeft;

    GameState() {
        this(true, true, true, true);
    }

    GameState(boolean boatIsLeft, boolean seedsIsLeft, boolean goatIsLeft, boolean wolfIsLeft) {
        this.boatIsLeft = boatIsLeft;
        this.seedsIsLeft = seedsIsLeft;
        this.goatIsLeft = goatIsLeft;
        this.wolfIsLeft = wolfIsLeft;
    }

    GameState(GameState state) {
        this(state.boatIsLeft, state.seedsIsLeft, state.goatIsLeft, state.wolfIsLeft);
    }

    boolean isValid() {
        if (seedsIsLeft == goatIsLeft) {
            if (goatIsLeft != boatIsLeft) {
                return false;
            }
        }
        if (goatIsLeft == wolfIsLeft) {
            if (wolfIsLeft != boatIsLeft) {
                return false;
            }
        }
        return true;
    }

    boolean isGoal() {
        return !seedsIsLeft && !goatIsLeft && !wolfIsLeft;
    }

    Stream<GameState> next() {
        final var list = new ArrayList<GameState>(4);
        list.add(new GameState(!boatIsLeft, seedsIsLeft, goatIsLeft, wolfIsLeft));
        if (boatIsLeft == seedsIsLeft) {
            list.add(new GameState(!boatIsLeft, !seedsIsLeft, goatIsLeft, wolfIsLeft));
        }
        if (boatIsLeft == goatIsLeft) {
            list.add(new GameState(!boatIsLeft, seedsIsLeft, !goatIsLeft, wolfIsLeft));
        }
        if (boatIsLeft == wolfIsLeft) {
            list.add(new GameState(!boatIsLeft, seedsIsLeft, goatIsLeft, !wolfIsLeft));
        }
        return list.stream().filter(GameState::isValid);
    }

    @Override
    public int hashCode() {
        return (boatIsLeft ? 0 : 1) ^ (seedsIsLeft ? 0 : 2) ^ (goatIsLeft ? 0 : 4) ^ (wolfIsLeft ? 0 : 8);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GameState) {
            final var other = (GameState) obj;
            return boatIsLeft == other.boatIsLeft && seedsIsLeft == other.seedsIsLeft && goatIsLeft == other.goatIsLeft && wolfIsLeft == other.wolfIsLeft;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        final var left = new ArrayList<String>();
        final var right = new ArrayList<String>();
        (boatIsLeft ? left : right).add("boat");
        (seedsIsLeft ? left : right).add("seeds");
        (goatIsLeft ? left : right).add("goat");
        (wolfIsLeft ? left : right).add("wolf");
        return String.join(" ", left) + " | " + String.join(" ", right);
    }
}
