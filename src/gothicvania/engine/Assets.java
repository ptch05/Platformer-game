package gothicvania.engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Loads and caches files from the {@code assets/} folder. The folder is looked up
 * next to the working directory first and then next to the compiled classes, so
 * the game runs whether it is started from the project root or from {@code out/}.
 * A missing image is replaced with a magenta placeholder instead of crashing.
 */
public final class Assets {
    private static final Map<String, BufferedImage> IMAGES = new HashMap<>();
    private static final Map<String, Sprite> SPRITES = new HashMap<>();
    private static Path root;

    private Assets() {
    }

    public static Path root() {
        if (root == null) {
            String override = System.getProperty("gv.assets");
            Path[] candidates = {
                override == null ? null : Path.of(override),
                Path.of("assets"),
                Path.of("..", "assets"),
                codeLocation().resolve("assets"),
                codeLocation().resolveSibling("assets"),
            };
            for (Path candidate : candidates) {
                if (candidate != null && Files.isDirectory(candidate)) {
                    root = candidate.toAbsolutePath().normalize();
                    break;
                }
            }
            if (root == null) throw new IllegalStateException("Could not find the assets folder; run from the project root.");
        }
        return root;
    }

    /** Looks for a sibling folder of {@code assets/} (e.g. {@code levels/}). */
    public static Path projectFile(String name) {
        return root().resolveSibling(name);
    }

    private static Path codeLocation() {
        try {
            return Path.of(Assets.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (Exception e) {
            return Path.of(".");
        }
    }

    public static InputStream open(String path) throws IOException {
        return Files.newInputStream(root().resolve(path));
    }

    public static synchronized BufferedImage image(String path) {
        return IMAGES.computeIfAbsent(path, Assets::loadImage);
    }

    private static BufferedImage loadImage(String path) {
        try (InputStream in = open(path)) {
            BufferedImage raw = ImageIO.read(in);
            BufferedImage argb = new BufferedImage(raw.getWidth(), raw.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = argb.createGraphics();
            g.drawImage(raw, 0, 0, null);
            g.dispose();
            return argb;
        } catch (IOException | RuntimeException e) {
            System.err.println("Missing image " + path + ": " + e.getMessage());
            BufferedImage placeholder = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = placeholder.createGraphics();
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, 16, 16);
            g.dispose();
            return placeholder;
        }
    }

    /**
     * A horizontal strip of equally sized frames.
     *
     * @param frameWidth  width of one frame in pixels
     * @param anchorX     x of the sprite's pivot (usually the feet) inside a frame
     * @param anchorY     y of the pivot inside a frame
     * @param facesRight  whether the art is drawn facing right
     */
    public static synchronized Sprite sprite(String path, int frameWidth, int anchorX, int anchorY, boolean facesRight) {
        String key = path + '@' + frameWidth + ',' + anchorX + ',' + anchorY + ',' + facesRight;
        return SPRITES.computeIfAbsent(key, k -> new Sprite(image(path), frameWidth, anchorX, anchorY, facesRight));
    }
}
