package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Lighting;
import gothicvania.engine.Particles;
import gothicvania.engine.Sprite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * A fireball thrown by ghosts and the demon. It can be ducked under, dashed
 * through, or knocked back with a well-timed sword swing, after which it burns
 * whatever it hits on the way back.
 */
public final class Fireball extends Entity {
    private static final Sprite SPRITE = Assets.sprite("sprites/fx/fireball.png", 52, 8, 7, false);
    private static final double SIZE = 8;

    private final int damage;
    private boolean reflected;
    private double life = 5;
    private double time;

    public Fireball(double cx, double cy, double vx, double vy, int damage) {
        super(cx - SIZE / 2, cy - SIZE / 2, SIZE, SIZE);
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
    }

    public boolean isReflected() {
        return reflected;
    }

    public int damage() {
        return damage;
    }

    /** Sends it back the way the hero is facing, faster. */
    public void reflect(int dir) {
        if (reflected) return;
        reflected = true;
        double speed = Math.max(220, Math.hypot(vx, vy) * 1.6);
        vx = dir * speed;
        vy = 0;
        life = 3;
        Audio.get().play("sounds/player/attack", 1f, 1.4f);
        level.hitstop(0.05);
        level.particles.emit(cx(), cy(), 12, Particles.burst().speed(40, 140).life(0.2, 0.4)
            .colors(0xFFFFFF, 0xFFE27A, 0xFF9A2E));
    }

    public void burst() {
        removed = true;
        level.particles.emit(cx(), cy(), 14, Particles.burst().speed(20, 110).life(0.2, 0.5).gravity(-40)
            .colors(0xFFF27A, 0xFFB02E, 0xE8461E, 0x7A1E10).size(1, 3));
    }

    @Override
    public void update(double dt) {
        time += dt;
        life -= dt;
        x += vx * dt;
        y += vy * dt;
        if (life <= 0 || level.map.solidIn(x + 2, y + 2, w - 4, h - 4)) {
            burst();
            return;
        }
        if (time % 0.04 < dt) {
            level.particles.emit(cx(), cy(), 1, Particles.burst().speed(5, 20).life(0.15, 0.35)
                .colors(reflected ? 0x9CF6FF : 0xFFB02E, 0xE8461E).size(1, 2));
        }
    }

    @Override
    public void render(Graphics2D g) {
        AffineTransform old = g.getTransform();
        double angle = Math.atan2(vy, vx) - Math.PI; // the art points left
        g.rotate(angle, cx(), cy());
        SPRITE.draw(g, SPRITE.frameAt(time, 0.05, true), cx(), cy(), false);
        g.setTransform(old);
    }

    @Override
    public void emitLight(Lighting lighting, int camX, int camY) {
        lighting.light(cx() - camX, cy() - camY, 46, 0.9f, reflected ? 0x6FE8FF : 0xFF7A20);
    }

    @Override
    public int layer() {
        return 40;
    }
}
