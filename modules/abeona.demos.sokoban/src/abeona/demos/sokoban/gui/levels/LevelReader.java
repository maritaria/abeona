package abeona.demos.sokoban.gui.levels;

import abeona.demos.sokoban.Position;
import abeona.demos.sokoban.SokobanLevel;
import abeona.demos.sokoban.SokobanState;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LevelReader {
    public static final char CHAR_FLOOR = '.';
    public static final char CHAR_WALL = '#';
    public static final char CHAR_BUTTON = '+';
    public static final char CHAR_BOX = '$';
    public static final char CHAR_PLAYER = '@';

    public static SokobanState readLevel(String path) {
        final var prefix = "abeona/demos/sokoban/gui/levels/";
        final var stream = LevelReader.class.getClassLoader().getResourceAsStream(prefix + path);
        final var lines = readStream(stream).filter(Predicate.not(String::isEmpty)).collect(Collectors.toList());
        if (lines.stream().map(line -> line.length()).distinct().count() != 1) {
            throw new RuntimeException("Not all lines have the same length");
        }
        final var width = lines.get(0).length();
        final var height = lines.size();
        final var level = new SokobanLevel(width, height);
        Position playerPos = null;
        final var boxes = new ArrayList<Position>();
        for (int y = 0; y < height; y++) {
            final var line = lines.get(y);
            for (int x = 0; x < width; x++) {
                final var pos = new Position(x, y);
                final var elem = line.charAt(x);
                switch (elem) {
                    case CHAR_FLOOR:
                        break;
                    case CHAR_WALL:
                        level.setWall(x, y, true);
                        break;
                    case CHAR_BUTTON:
                        level.addButton(pos);
                        break;
                    case CHAR_BOX:
                        boxes.add(pos);
                        break;
                    case CHAR_PLAYER:
                        if (playerPos == null) {
                            playerPos = pos;
                        } else {
                            throw new RuntimeException("Level has multiple player spawns (" + CHAR_PLAYER + ")");
                        }
                        break;
                }
            }
        }
        if (playerPos == null) {
            throw new RuntimeException("Level does not specify a player location (" + CHAR_PLAYER + ")");
        }
        return new SokobanState(level, new HashSet<>(boxes), playerPos);
    }

    private static Stream<String> readStream(InputStream inputStream) {
        //creating an InputStreamReader object
        final var isReader = new InputStreamReader(inputStream);
        //Creating a BufferedReader object
        final var reader = new BufferedReader(isReader);
        return reader.lines();
    }
}
