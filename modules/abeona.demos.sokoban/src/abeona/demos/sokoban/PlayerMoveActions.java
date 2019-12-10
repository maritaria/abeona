package abeona.demos.sokoban;

import abeona.Transition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static abeona.demos.sokoban.Helpers.*;

public class PlayerMoveActions {
    static List<GameAction> actions = Arrays.asList(
            new MoveLeft(),
            new MoveRight(),
            new MoveUp(),
            new MoveDown(),
            new PushLeft(),
            new PushRight(),
            new PushDown(),
            new PushUp());

    public static Stream<Transition<SokobanState>> nextStates(SokobanState state) {
        return actions.stream()
                .filter(gameAction -> gameAction.canPerform(state))
                .flatMap(gameAction -> gameAction.resultingStates(state))
                .map(nextState -> new Transition<>(state, nextState));
    }

    abstract static class Move implements GameAction {
        protected abstract Position nextPosition(Position current);

        @Override
        public boolean canPerform(SokobanState state) {
            final var nextPos = nextPosition(state.getPlayer());
            return state.isOpen(nextPos);
        }

        @Override
        public Stream<SokobanState> resultingStates(SokobanState state) {
            final var newPos = nextPosition(state.getPlayer());
            return Stream.of(new SokobanState(state.getLevel(), state.getBoxes(), newPos));
        }

        @Override
        public String name() {
            return getClass().getSimpleName();
        }
    }

    public static class MoveLeft extends Move {
        @Override
        protected Position nextPosition(Position current) {
            return leftOf(current);
        }
    }

    public static class MoveRight extends Move {

        @Override
        protected Position nextPosition(Position current) {
            return rightOf(current);
        }
    }

    public static class MoveUp extends Move {
        @Override
        protected Position nextPosition(Position current) {
            return above(current);
        }
    }

    public static class MoveDown extends Move {
        @Override
        protected Position nextPosition(Position current) {
            return below(current);
        }
    }

    abstract static class Push implements GameAction {
        protected static class PushInfo {
            Position player;
            Position boxBefore;
            Position boxAfter;

            PushInfo(Position player, Position boxBefore, Position boxAfter) {
                this.player = player;
                this.boxBefore = boxBefore;
                this.boxAfter = boxAfter;
            }
        }

        @Override
        public boolean canPerform(SokobanState state) {
            final var info = getPushInfo(state);
            return state.getPlayer().equals(info.player) && state.isBox(info.boxBefore) && state.isOpen(info.boxAfter);
        }

        @Override
        public Stream<SokobanState> resultingStates(SokobanState state) {
            final var info = getPushInfo(state);
            final var boxes = new HashSet<>(state.getBoxes());
            boxes.remove(info.boxBefore);
            boxes.add(info.boxAfter);
            return Stream.of(new SokobanState(state.getLevel(), boxes, info.boxBefore));
        }

        @Override
        public String name() {
            return getClass().getSimpleName();
        }

        protected abstract PushInfo getPushInfo(SokobanState state);
    }

    public static class PushLeft extends Push {
        @Override
        protected PushInfo getPushInfo(SokobanState state) {
            final var player = state.getPlayer();
            final var boxBefore = leftOf(player);
            final var boxAfter = leftOf(boxBefore);
            return new PushInfo(player, boxBefore, boxAfter);
        }
    }

    public static class PushRight extends Push {
        @Override
        protected PushInfo getPushInfo(SokobanState state) {
            final var player = state.getPlayer();
            final var boxBefore = rightOf(player);
            final var boxAfter = rightOf(boxBefore);
            return new PushInfo(player, boxBefore, boxAfter);
        }
    }

    public static class PushUp extends Push {
        @Override
        protected PushInfo getPushInfo(SokobanState state) {
            final var player = state.getPlayer();
            final var boxBefore = above(player);
            final var boxAfter = above(boxBefore);
            return new PushInfo(player, boxBefore, boxAfter);
        }
    }

    public static class PushDown extends Push {
        @Override
        protected PushInfo getPushInfo(SokobanState state) {
            final var player = state.getPlayer();
            final var boxBefore = below(player);
            final var boxAfter = below(boxBefore);
            return new PushInfo(player, boxBefore, boxAfter);
        }
    }
}
