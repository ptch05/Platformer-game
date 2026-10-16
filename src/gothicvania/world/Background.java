package gothicvania.world;

import gothicvania.engine.Assets;
import gothicvania.engine.Display;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** Draws a theme's parallax layers behind the level. */
public final class Background {
    private final Theme theme;
    private final double referenceY;

    /**
     * @param levelHeight height of the level in pixels; layers line up with the
     *                    camera resting at the bottom of the level
     */
    public Background(Theme theme, int levelHeight) {
        this.theme = theme;
        this.referenceY = levelHeight - Display.HEIGHT;
    }

    public void render(Graphics2D g, double camX, double camY) {
        for (Theme.Layer layer : theme.layers) {
            BufferedImage img = Assets.image(layer.image());
            int w = img.getWidth(), h = img.getHeight();
            switch (layer.anchor()) {
                case FILL -> {
                    double scale = Math.max(Display.WIDTH / (double) w, Display.HEIGHT / (double) h);
                    int sw = (int) Math.ceil(w * scale), sh = (int) Math.ceil(h * scale);
                    int x = -Math.floorMod((long) (camX * layer.parallaxX()), sw);
                    for (int dx = x; dx < Display.WIDTH; dx += sw) g.drawImage(img, dx, Display.HEIGHT - sh, sw, sh, null);
                }
                case BOTTOM, TOP -> {
                    double dy = (camY - referenceY) * layer.parallaxY();
                    int y = layer.anchor() == Theme.Layer.Anchor.BOTTOM
                        ? (int) Math.round(Display.HEIGHT - h + layer.offsetY() - dy)
                        : (int) Math.round(layer.offsetY() - dy);
                    int x = -Math.floorMod((long) (camX * layer.parallaxX()), w);
                    for (int dx = x; dx < Display.WIDTH; dx += w) g.drawImage(img, dx, y, null);
                    if (layer.anchor() == Theme.Layer.Anchor.BOTTOM && y + h < Display.HEIGHT) {
                        // the camera is above the art: extend it downwards with its own bottom colour
                        g.setColor(new Color(img.getRGB(w / 2, h - 1), true));
                        g.fillRect(0, y + h, Display.WIDTH, Display.HEIGHT - (y + h));
                    }
                }
            }
        }
    }
}
