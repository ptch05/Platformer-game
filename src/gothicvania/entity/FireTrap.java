package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Lighting;
import gothicvania.engine.Particles;
import gothicvania.engine.Sprite;
import java.awt.Graphics2D;

/**
 * A floor vent that erupts in a pillar of flame on a fixed rhythm. It sputters
 * sparks just before it fires, so the timing can be read. Traps placed in a boss
 * arena stay dormant until the boss wakes them.
 */
public final class FireTrap extends Entity {
    private static final Sprite SPRITE = Assets.sprite("sprites/fx/fire-trap.png", 16, 8, 34, true);
    private static final double PERIOD = 2.8, WARN_AT = 1.75, FIRE_AT = 2.2, SMOKE_AT = 2.55;

    private final boolean dormantUntilWoken;
    private boolean awake;
    private double clock;

    public FireTrap(double footX, double footY, double phase, boolean bossControlled) {
        super(footX - 5, footY - 32, 10, 30);
        this.clock = phase;
        this.dormantUntilWoken = bossControlled;
        this.awake = !bossControlled;
    }

    public boolean isBossControlled() {
        return dormantUntilWoken;
    }

    public void wake(double phase) {
        awake = true;
        clock = phase;
    }

    public void sleep() {
        if (dormantUntilWoken) awake = false;
    }

    public boolean isBurning() {
        return awake && clock >= FIRE_AT && clock < SMOKE_AT;
    }

    @Override
    public void update(double dt) {
        if (!awake) return;
        double before = clock;
        clock = (clock + dt) % PERIOD;
        if (clock >= WARN_AT && clock < FIRE_AT && (int) (clock / 0.06) != (int) (before / 0.06)) {
            level.particles.emit(cx(), bottom() - 2, 1, Particles.burst().speed(20, 60).direction(-90, 50)
                .life(0.2, 0.4).colors(0xFFE27A, 0xFF8A2E));
        }
        if (before < FIRE_AT && clock >= FIRE_AT) {
            double dist = Math.abs(level.player().cx() - cx());
            if (dist < 260) Audio.get().play("sounds/misc/fire-trap", (float) (1 - dist / 260) * 0.8f, 1f);
        }
        if (isBurning() && clock % 0.05 < dt) {
            level.particles.emit(cx(), y + 4, 2, Particles.burst().speed(10, 40).direction(-90, 70).gravity(-60)
                .life(0.3, 0.6).colors(0xFFB02E, 0xE8461E, 0xFFF27A));
        }
    }

    private int frame() {
        if (!awake || clock < WARN_AT) return 0;
        if (clock < FIRE_AT) return 1 + (int) (clock / 0.08) % 2;
        if (clock < SMOKE_AT) return clock < FIRE_AT + 0.14 ? 3 : 4;
        return clock < SMOKE_AT + 0.14 ? 5 : 6;
    }

    @Override
    public void render(Graphics2D g) {
        SPRITE.draw(g, frame(), cx(), bottom() + 2, true);
    }

    @Override
    public void emitLight(Lighting lighting, int camX, int camY) {
        if (isBurning()) lighting.light(cx() - camX, cy() - camY, 70, 1f, 0xFF7A20);
        else if (awake && clock >= WARN_AT && clock < FIRE_AT) lighting.light(cx() - camX, bottom() - camY, 26, 0.6f, 0xFF7A20);
        else lighting.light(cx() - camX, bottom() - camY, 12, 0.4f, 0xFF4A20);
    }

    @Override
    public int layer() {
        return 15;
    }
}
