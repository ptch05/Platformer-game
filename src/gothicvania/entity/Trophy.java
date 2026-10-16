package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Lighting;
import gothicvania.engine.Particles;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** The chalice at the end of each level. Claiming it completes the level. */
public final class Trophy extends Entity {
    private static final BufferedImage IMAGE = Assets.image("sprites/items/trophy.png");
    private double time;
    private boolean claimed;
    private final boolean drops;

    /**
     * @param drops whether it falls from where it appears (the boss drops it) instead of floating
     */
    public Trophy(double footX, double footY, boolean drops) {
        super(footX - 10, footY - 26, 20, 26);
        this.drops = drops;
        if (drops) vy = -180;
    }

    @Override
    public void update(double dt) {
        time += dt;
        if (drops && !onGround) {
            vy = Math.min(400, vy + 900 * dt);
            move(dt);
        }
        if (time % 0.1 < dt) {
            level.particles.emit(cx() + (Math.random() - 0.5) * 20, bottom(), 1, Particles.burst().speed(10, 30)
                .direction(-90, 20).gravity(-20).life(0.6, 1.2).colors(0xFFE27A, 0xFFFFFF));
        }
        if (!claimed && overlaps(level.player()) && !level.player().isDead()) {
            claimed = true;
            removed = true;
            level.onTrophyClaimed();
        }
    }

    @Override
    public void render(Graphics2D g) {
        int beam = 30 + (int) (Math.sin(time * 2) * 6);
        g.setColor(new Color(255, 226, 122, beam));
        g.fillRect((int) x + 2, (int) bottom() - 120, (int) w - 4, 120);
        double bob = Math.sin(time * 2.5) * 2 - 2;
        g.drawImage(IMAGE, (int) Math.round(cx() - IMAGE.getWidth() / 2.0), (int) Math.round(bottom() - IMAGE.getHeight() + bob), null);
    }

    @Override
    public void emitLight(Lighting lighting, int camX, int camY) {
        lighting.light(cx() - camX, cy() - camY, 80, 1f, 0xFFE27A);
    }

    @Override
    public int layer() {
        return 25;
    }
}
