package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.world.Theme;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * A ledge that glides back and forth between two points, easing at each end,
 * and carries whoever stands on it.
 */
public final class MovingPlatform extends Platform {
    private static final double SPEED = 55;

    private final double ax, ay, bx, by;
    private final double period;
    private final BufferedImage image;
    private final Theme theme;
    private double time;

    /** Endpoints are the top-left positions of the platform at either end of its path. */
    public MovingPlatform(double ax, double ay, double bx, double by, double phase, Theme theme) {
        super(ax, ay, 64, 8);
        this.ax = ax;
        this.ay = ay;
        this.bx = bx;
        this.by = by;
        this.theme = theme;
        double length = Math.max(1, Math.hypot(bx - ax, by - ay));
        this.period = 2 * length / SPEED * 1.4;
        this.time = phase * period;
        this.image = theme == Theme.INFERNO ? null : Assets.image("tiles/moving-platform.png");
        place();
    }

    private void place() {
        double t = 0.5 - 0.5 * Math.cos(time / period * Math.PI * 2);
        x = ax + (bx - ax) * t;
        y = ay + (by - ay) * t;
    }

    @Override
    public void update(double dt) {
        double ox = x, oy = y;
        time += dt;
        place();
        carryRiders(x - ox, y - oy);
    }

    @Override
    public void render(Graphics2D g) {
        int px = (int) Math.round(x), py = (int) Math.round(y);
        if (image != null) {
            // the art is 86px wide with a grassy lip; centre it on the 64px collision box
            g.drawImage(image, px - 11, py - 3, null);
        } else {
            BufferedImage tiles = Assets.image(theme.tiles());
            for (int i = 0; i < 4; i++) {
                g.drawImage(tiles, px + i * 16, py, px + i * 16 + 16, py + 12, i * 16, 0, i * 16 + 16, 12, null);
            }
            g.setColor(new Color(10, 4, 8));
            g.fillRect(px, py + 12, 64, 2);
            g.setColor(new Color(60, 52, 64));
            for (int cx : new int[]{px + 6, px + 56}) {
                for (int cy = py - 2; cy > py - 120; cy -= 4) g.fillRect(cx, cy, 2, 2);
            }
        }
    }
}
