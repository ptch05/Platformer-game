package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Lighting;
import gothicvania.engine.Particles;
import gothicvania.engine.Sprite;
import java.awt.Graphics2D;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A lantern-bearing ghost that drifts through walls near where it haunts and
 * hurls fireballs at the hero. Its lantern flares before each throw. The wraith
 * (haloed) variant fires a spread of three and blinks away when struck.
 */
public final class Ghost extends Enemy {
    private static final Sprite FLOAT = Assets.sprite("sprites/ghost/float.png", 37, 18, 35, true);
    private static final Sprite HALO = Assets.sprite("sprites/ghost/halo.png", 37, 18, 35, true);
    private static final double LEASH = 100;

    private enum State { DRIFT, CHARGE, VANISH, APPEAR }

    private final boolean wraith;
    private final double homeX, homeY;
    private State state = State.DRIFT;
    private double stateTime;
    private double cooldown;
    private double bobPhase = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
    private double baseY;

    public Ghost(double cx, double cy, boolean wraith) {
        super(cx - 9, cy - 17, 18, 34, wraith ? 4 : 2, 15, wraith ? 5 : 2);
        this.wraith = wraith;
        homeX = cx;
        homeY = cy;
        baseY = y;
        cooldown = 1.0 + ThreadLocalRandom.current().nextDouble();
    }

    @Override
    public boolean isHarmful() {
        return super.isHarmful() && state != State.VANISH && !(state == State.APPEAR && stateTime < 0.2);
    }

    @Override
    public boolean isVulnerable() {
        return super.isVulnerable() && state != State.VANISH && state != State.APPEAR;
    }

    @Override
    protected void think(double dt) {
        stateTime += dt;
        cooldown -= dt;
        Player p = player();
        boolean inRange = !p.isDead() && Math.abs(p.cx() - cx()) < 230 && Math.abs(p.cy() - cy()) < 130;
        if (inRange) facing = dirToPlayer();

        switch (state) {
            case DRIFT -> {
                double target = inRange ? p.cx() - facing * 80 : homeX;
                target = Math.max(homeX - LEASH, Math.min(homeX + LEASH, target));
                double speed = stun > 0 ? 0 : 28;
                vx = Math.abs(target - cx()) < 4 ? 0 : Math.signum(target - cx()) * speed;
                if (inRange && cooldown <= 0) setState(State.CHARGE);
            }
            case CHARGE -> {
                vx = 0;
                if (stateTime % 0.05 < dt) {
                    level.particles.emit(lanternX(), lanternY(), 2, Particles.burst().speed(10, 30).life(0.2, 0.4)
                        .colors(0xFFE27A, 0xFF8A2E).gravity(-30));
                }
                if (stateTime > 0.6) {
                    shoot();
                    cooldown = wraith ? 2.0 : 2.6;
                    setState(State.DRIFT);
                }
            }
            case VANISH -> {
                vx = 0;
                if (stateTime > 0.3) {
                    blink();
                    setState(State.APPEAR);
                }
            }
            case APPEAR -> {
                if (stateTime > 0.3) setState(State.DRIFT);
            }
        }
        x += vx * dt;
        baseY += (homeY - 17 - baseY) * Math.min(1, dt * 2);
        y = baseY + Math.sin(time * 2.3 + bobPhase) * 5;
    }

    private void shoot() {
        Player p = player();
        double angle = Math.atan2(p.cy() - 4 - lanternY(), p.cx() - lanternX());
        int count = wraith ? 3 : 1;
        for (int i = 0; i < count; i++) {
            double a = angle + (i - (count - 1) / 2.0) * Math.toRadians(16);
            level.spawn(new Fireball(lanternX(), lanternY(), Math.cos(a) * 125, Math.sin(a) * 125, 15));
        }
        Audio.get().playVaried("sounds/misc/fireball", 0.6f);
    }

    /** Wraiths reappear somewhere else near the hero. */
    private void blink() {
        Player p = player();
        double side = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
        double nx = p.cx() + side * (70 + ThreadLocalRandom.current().nextDouble(40));
        nx = Math.max(homeX - LEASH * 1.6, Math.min(homeX + LEASH * 1.6, nx));
        x = nx - w / 2;
        cooldown = 0.7;
    }

    private void setState(State s) {
        state = s;
        stateTime = 0;
    }

    @Override
    protected void onHurt(int dir) {
        stun = 0.3;
        if (wraith && hp > 0) setState(State.VANISH);
        else if (state == State.CHARGE) setState(State.DRIFT);
    }

    @Override
    protected int[] bloodColors() {
        return new int[]{0x8C6CD8, 0x5A3E9C, 0x3EC6E8};
    }

    private double lanternX() {
        return cx() + facing * 12;
    }

    private double lanternY() {
        return cy() + 13;
    }

    @Override
    public void render(Graphics2D g) {
        float alpha = switch (state) {
            case VANISH -> (float) Math.max(0, 1 - stateTime / 0.3);
            case APPEAR -> (float) Math.min(1, stateTime / 0.3);
            default -> 0.88f;
        };
        Sprite sprite = wraith ? HALO : FLOAT;
        sprite.draw(g, sprite.frameAt(time, 0.14, true), cx(), cy(), facing > 0, flash > 0, alpha);
    }

    @Override
    public void emitLight(Lighting lighting, int camX, int camY) {
        if (state == State.VANISH) return;
        float flare = state == State.CHARGE ? (float) Math.min(1, 0.6 + stateTime) : 0.6f;
        lighting.light(lanternX() - camX, lanternY() - camY, state == State.CHARGE ? 60 : 40, flare, 0xFF8A3A);
        if (wraith) lighting.light(cx() - camX, cy() - camY, 34, 0.4f, 0x3EC6E8);
    }
}
