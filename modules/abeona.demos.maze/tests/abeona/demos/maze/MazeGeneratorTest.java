package abeona.demos.maze;

import org.junit.jupiter.api.Test;

import java.util.Random;

class MazeGeneratorTest {
    @Test
    void generate() {
        final var maze = new MazeGenerator(new Random(1))
                .createMazeDfs(25, 25);
        new MazeRenderer(maze).paintWalls(3, 3).save("random.maze.png");
    }
}