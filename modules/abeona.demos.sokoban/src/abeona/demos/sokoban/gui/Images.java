package abeona.demos.sokoban.gui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Images {
    private static final String namespace = "nl/kenney/sokoban/png/";
    public static final BufferedImage floor = load(namespace + "ground/ground_01.png");
    public static final BufferedImage button = load(namespace + "environment/environment_04.png");
    public static final BufferedImage box = load(namespace + "crates/crate_43.png");
    public static final BufferedImage player = load(namespace + "player/player_05.png");
    public static final BufferedImage wall = load(namespace + "blocks/block_06.png");

    private static BufferedImage load(String path) {
        final var url = Images.class.getClassLoader().getResource(path);
        try {
            return ImageIO.read(url);
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
        return null;
    }
}
