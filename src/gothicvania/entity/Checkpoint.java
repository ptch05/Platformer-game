package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Lighting;
import gothicvania.engine.Particles;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * A hooded statue. Touching it lights a soul-flame above its head, restores the
 * hero's health and makes it the place to return to after dying.
 */
public final class Checkpoint extends Entity {
    private static final BufferedImage STATUE = Assets.image("props/statue.png");
    public final int id;
    private boolean lit;
    private double time;

    public Checkpoint(double footX, double footY, int id, boolean lit) {
        super(footX - 12, footY - 50, 24, 50);
        this.id = id;
        this.lit = lit;
    }

    @Override
    public void update(double dt) {
        time += dt;
        Player p = level.player();
        if (!lit && overlaps(p) && !p.isDead()) {
            lit = true;
            p.heal(p.maxHp);
            Audio.get().play("sounds/collectibles/armour", 0.7f, 0.8f);
            level.onCheckpoint(this);
            level.banner(null, "CHECKPOINT - HEALTH RESTORED");
            level.particles.emit(cx(), y + 6, 30, Particles.burst().speed(20, 90).life(0.4, 1.0).gravity(-60)
                .colors(0x9CF6FF, 0x3EC6E8, 0xFFFFFF));
        }
        if (lit && time % 0.08 < dt) {
            level.particles.emit(flameX(), flameY(), 1, Particles.burst().speed(8, 24).direction(-90, 40).gravity(-30)
                .life(0.3, 0.7).colors(0x9CF6FF, 0x3EC6E8));
        }
    }

    private double flameX() {
        return cx() + 3;
    }

    private double flameY() {
        return bottom() - STATUE.getHeight() + 2;
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage(STATUE, (int) Math.round(cx() - STATUE.getWidth() / 2.0), (int) Math.round(bottom() - STATUE.getHeight() + 1), null);
        if (lit) {
            int fx = (int) flameX(), fy = (int) flameY();
            int h = 4 + (int) (Math.sin(time * 14) * 1.5);
            g.setColor(new Color(0x3EC6E8));
            g.fillRect(fx - 2, fy - h, 4, h);
            g.setColor(new Color(0xE8FFFF));
            g.fillRect(fx - 1, fy - h + 2, 2, h - 2);
        }
    }

    @Override
    public void emitLight(Lighting lighting, int camX, int camY) {
        if (lit) lighting.light(flameX() - camX, flameY() - camY, 70, 0.9f, 0x3EC6E8);
    }

    @Override
    public int layer() {
        return 3;
    }
}
