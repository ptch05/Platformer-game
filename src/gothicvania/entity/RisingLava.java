package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Display;
import gothicvania.engine.Lighting;
import gothicvania.engine.Particles;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * A sea of lava that starts climbing once the hero passes a trigger column and
 * doesn't stop until it reaches its high-water mark. Touching it is fatal.
 */
public final class RisingLava extends Entity {
    private static final Color DEEP = new Color(169, 59, 59);
    private final double triggerX;
    private final double stopY;
    private final BufferedImage strip = Assets.image("sprites/fx/lava.png");
    private boolean rising;
    private double speed = 16;
    private double time;

    public RisingLava(double triggerX, double startY, double stopY, double levelWidth) {
        super(0, startY, levelWidth, 10_000);
        this.triggerX = triggerX;
        this.stopY = stopY;
    }

    @Override
    public void update(double dt) {
        time += dt;
        Player p = level.player();
        if (!rising && p.cx() > triggerX && !p.isDead()) {
            rising = true;
            Audio.get().play("sounds/demon/demon-idle", 0.8f, 0.6f);
            level.banner("THE DEPTHS STIR", "CLIMB!");
            level.camera.shake(0.5);
        }
        if (rising && y > stopY) {
            speed = Math.min(30, speed + dt * 0.8);
            y = Math.max(stopY, y - speed * dt);
            if (time % 0.5 < dt) level.camera.shake(0.08);
        }
        if (!p.isDead() && p.bottom() > y + 6) p.die();
        if (time % 0.06 < dt) {
            double ex = level.camera.rawX() + Math.random() * Display.WIDTH;
            level.particles.emit(ex, y, 1, Particles.burst().speed(20, 60).direction(-90, 40).gravity(-20)
                .life(0.6, 1.4).colors(0xFFB02E, 0xFF6A1E, 0xFFF27A));
        }
    }

    @Override
    public void render(Graphics2D g) {
        int top = (int) Math.round(y);
        int camX = level.camera.x(), camY = level.camera.y();
        if (top > camY + Display.HEIGHT) return;
        int frame = (int) (time / 0.17) % 6;
        int sx = frame * 128;
        for (int dx = Math.floorDiv(camX, 128) * 128; dx < camX + Display.WIDTH; dx += 128) {
            g.drawImage(strip, dx, top, dx + 128, top + 63, sx, 0, sx + 128, 63, null);
        }
        g.setColor(DEEP);
        g.fillRect(camX, top + 63, Display.WIDTH, Math.max(0, camY + Display.HEIGHT - top - 63));
    }

    @Override
    public void emitLight(Lighting lighting, int camX, int camY) {
        double sy = y - camY;
        if (sy > Display.HEIGHT + 60) return;
        for (int i = 0; i <= 4; i++) lighting.light(i * Display.WIDTH / 4.0, sy + 10, 110, 0.9f, i % 2 == 0 ? 0xFF6A1E : -1);
    }

    @Override
    public int layer() {
        return 50;
    }
}
