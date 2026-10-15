package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Lighting;
import gothicvania.engine.Particles;
import gothicvania.engine.Sprite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Malphas, the chained demon: the final boss.
 *
 * <p>A three-phase fight in a sealed arena. Every attack has a readable tell and
 * a counter:
 * <ul>
 *   <li><b>Volley</b> - its hands flare, then fireballs fly at you. Duck, dash, or swing to send them back.</li>
 *   <li><b>Dive</b> - it rises and flashes, then swoops at where you stand. Dodge it and it is left
 *       stunned on the ground taking double damage.</li>
 *   <li><b>Breath</b> - it lands at the edge of the arena and spews soul-fire across the floor.
 *       Get above it on a ledge or dash through.</li>
 *   <li><b>Summon</b> (phase 2+) - skeletons claw up from the arena floor.</li>
 *   <li><b>Fire rain</b> (phase 2+) - embers mark the floor, then fire falls on them.</li>
 * </ul>
 * Phase 3 wakes the arena's fire vents and chains attacks together.
 */
public final class Demon extends Enemy {
    public static final String NAME = "MALPHAS, THE CHAINED";
    private static final Sprite IDLE = Assets.sprite("sprites/demon/idle.png", 233, 134, 160, false);
    private static final Sprite ATTACK = Assets.sprite("sprites/demon/attack.png", 233, 134, 160, false);
    private static final double BREATH_FRAME = 0.08;
    private static final int MAX_HP = 44;
    /** How far above the floor its feet float: low enough to reach with a jump, high enough to walk under. */
    private static final double HOVER_HEIGHT = 66;

    private enum State { DORMANT, INTRO, HOVER, VOLLEY_CHARGE, VOLLEY, DIVE_RISE, DIVE, DIVE_REST, BREATH_MOVE, BREATH, SUMMON, RAIN, ENRAGE, DYING }

    private State state = State.DORMANT;
    private State lastAttack;
    private double stateTime;
    private int phase = 1;
    private int shotsFired;
    private double targetX, targetY;
    private double arenaLeft, arenaRight, floorY, ceilingY;
    private final List<double[]> rainMarks = new ArrayList<>();
    private boolean comboPending;

    public Demon(double footX, double footY) {
        super(footX - 20, footY - 70, 40, 70, MAX_HP, 20, 0);
        facing = -1;
    }

    /** Starts the fight inside the given arena (world pixels). */
    public void awaken(double left, double right, double floor, double ceiling) {
        if (state != State.DORMANT) return;
        arenaLeft = left;
        arenaRight = right;
        floorY = floor;
        ceilingY = ceiling;
        setState(State.INTRO);
        Audio.get().play("sounds/demon/demon-idle", 1f, 0.85f);
        level.camera.shake(0.6);
        level.banner(NAME, "THE FINAL TRIAL");
    }

    public boolean isAwake() {
        return state != State.DORMANT;
    }

    public boolean isDefeated() {
        return state == State.DYING || removed;
    }

    public int phase() {
        return phase;
    }

    @Override
    public boolean isHarmful() {
        return switch (state) {
            case DORMANT, INTRO, DIVE_REST, ENRAGE, DYING -> false;
            default -> hp > 0;
        };
    }

    @Override
    public boolean isVulnerable() {
        return switch (state) {
            case DORMANT, INTRO, ENRAGE, DYING -> false;
            default -> hp > 0;
        };
    }

    /** The flame breath's hitbox {x, y, w, h} while it burns, else null. */
    public double[] breathBox() {
        if (state != State.BREATH) return null;
        int frame = (int) (stateTime / BREATH_FRAME);
        if (frame < 7 || frame > 15) return null;
        double reach = 128;
        return new double[]{facing < 0 ? cx() - reach : cx() + 6, bottom() - 58, reach - 6, 66};
    }

    // ------------------------------------------------------------------ damage

    @Override
    public boolean strike(int swingId, int damage, double fromX) {
        return super.strike(swingId, state == State.DIVE_REST ? damage * 2 : damage, fromX);
    }

    @Override
    protected void onHurt(int dir) {
        level.hitstop(0.04);
        int newPhase = hp <= MAX_HP / 3 ? 3 : hp <= MAX_HP * 2 / 3 ? 2 : 1;
        if (newPhase > phase) {
            phase = newPhase;
            setState(State.ENRAGE);
        }
    }

    @Override
    protected void die() {
        setState(State.DYING);
        Audio.get().play("sounds/demon/demon-attack", 1f, 0.6f);
        level.slowMotion(0.3, 2.0);
        level.camera.shake(1);
        level.flash(0xFFFFFF, 0.2);
        rainMarks.clear();
    }

    @Override
    protected int[] bloodColors() {
        return new int[]{0x3EC6E8, 0x9CF6FF, 0x1A1420};
    }

    // ------------------------------------------------------------------ brain

    @Override
    protected void think(double dt) {
        stateTime += dt;
        Player p = player();
        if (state != State.DIVE && state != State.BREATH && state != State.DYING && state != State.DIVE_REST) {
            facing = p.cx() < cx() ? -1 : 1;
        }
        switch (state) {
            case DORMANT -> bob(dt);
            case INTRO -> {
                flyTowards(cx(), floorY - HOVER_HEIGHT, 40, dt);
                if (stateTime > 2.2) setState(State.HOVER);
            }
            case HOVER -> hover(dt);
            case VOLLEY_CHARGE -> {
                bob(dt);
                if (stateTime % 0.05 < dt) sparkHands(0xFF8A2E);
                if (stateTime > (phase == 3 ? 0.4 : 0.6)) {
                    shotsFired = 0;
                    setState(State.VOLLEY);
                }
            }
            case VOLLEY -> volley(dt);
            case DIVE_RISE -> {
                vx = 0;
                y -= 60 * dt;
                y = Math.max(ceilingY + 10, y);
                if (stateTime > 0.5) {
                    targetX = clampX(p.cx());
                    targetY = floorY;
                    setState(State.DIVE);
                    Audio.get().play("sounds/demon/demon-attack", 0.9f, 1.2f);
                }
            }
            case DIVE -> dive(dt);
            case DIVE_REST -> {
                if (stateTime % 0.25 < dt) {
                    level.particles.emit(cx(), y - 4, 3, Particles.burst().speed(10, 30).life(0.3, 0.6)
                        .colors(0xFFE27A, 0xFFFFFF));
                }
                if (stateTime > (phase == 1 ? 1.3 : phase == 2 ? 1.05 : 0.85)) {
                    setState(State.HOVER);
                    if (phase == 3 && comboPending) {
                        comboPending = false;
                        setState(State.VOLLEY_CHARGE);
                    }
                }
            }
            case BREATH_MOVE -> {
                boolean done = flyTowards(targetX, floorY, 220, dt);
                if (done || stateTime > 2.0) {
                    facing = p.cx() < cx() ? -1 : 1;
                    setState(State.BREATH);
                    Audio.get().play("sounds/demon/demon-attack", 1f, 1f);
                }
            }
            case BREATH -> breath(dt);
            case SUMMON -> {
                bob(dt);
                if (stateTime % 0.06 < dt) sparkHands(0x9CF6FF);
                if (stateTime > 0.8 && stateTime - dt <= 0.8) summon();
                if (stateTime > 1.3) setState(State.HOVER);
            }
            case RAIN -> rain(dt);
            case ENRAGE -> {
                bob(dt);
                if (stateTime < dt * 1.5) {
                    Audio.get().play("sounds/demon/demon-idle", 1f, phase == 3 ? 0.7f : 0.8f);
                    level.camera.shake(0.8);
                    level.flash(phase == 3 ? 0xFF2840 : 0x3EC6E8, 0.2);
                    level.banner(phase == 3 ? "MALPHAS IS ENRAGED" : "THE CHAINS BREAK", null);
                    if (distanceTo(p) < 110) p.hurt(10, cx());
                    level.particles.emit(cx(), cy(), 60, Particles.burst().speed(60, 240).drag(2).life(0.4, 1.0)
                        .colors(phase == 3 ? 0xFF2840 : 0x3EC6E8, 0xFFFFFF, 0x1A1420).size(1, 3));
                    rainMarks.clear();
                    if (phase == 3) level.wakeArenaTraps();
                }
                if (stateTime > 1.5) setState(State.HOVER);
            }
            case DYING -> dying(dt);
        }
    }

    private void hover(double dt) {
        Player p = player();
        double side = cx() < p.cx() ? -1 : 1;
        double tx = clampX(p.cx() + side * 95);
        double ty = floorY - HOVER_HEIGHT + Math.sin(time * 1.7) * 8;
        flyTowards(tx, ty, phase == 3 ? 110 : 85, dt);
        double hoverTime = phase == 1 ? 1.5 : phase == 2 ? 1.15 : 0.8;
        if (stateTime > hoverTime) chooseAttack();
        if (state == State.HOVER && stateTime % 1.5 < dt && Math.random() < 0.3) {
            Audio.get().play("sounds/demon/demon-idle", 0.35f, 1.1f);
        }
    }

    private void chooseAttack() {
        List<State> bag = new ArrayList<>();
        add(bag, State.VOLLEY_CHARGE, 4);
        add(bag, State.DIVE_RISE, 4);
        add(bag, State.BREATH_MOVE, 3);
        if (phase >= 2) {
            add(bag, State.RAIN, 3);
            if (level.countSkeletons() < 3) add(bag, State.SUMMON, 2);
        }
        bag.removeIf(s -> s == lastAttack);
        State next = bag.get(ThreadLocalRandom.current().nextInt(bag.size()));
        lastAttack = next;
        if (next == State.BREATH_MOVE) {
            // land on whichever side keeps the hero in front of the flames
            boolean playerLeft = player().cx() < (arenaLeft + arenaRight) / 2;
            targetX = playerLeft ? arenaRight - 60 : arenaLeft + 60;
        }
        if (next == State.DIVE_RISE) comboPending = phase == 3;
        setState(next);
    }

    private static void add(List<State> bag, State s, int weight) {
        for (int i = 0; i < weight; i++) bag.add(s);
    }

    private void volley(double dt) {
        bob(dt);
        int total = phase == 1 ? 3 : phase == 2 ? 4 : 5;
        double interval = phase == 3 ? 0.18 : 0.24;
        if (shotsFired < total && stateTime >= shotsFired * interval) {
            Player p = player();
            double hx = cx() + facing * 14, hy = cy() - 6;
            double angle = Math.atan2(p.cy() - hy, p.cx() - hx) + (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.25;
            double speed = phase == 3 ? 165 : 140;
            level.spawn(new Fireball(hx, hy, Math.cos(angle) * speed, Math.sin(angle) * speed, 15));
            Audio.get().playVaried("sounds/misc/fireball", 0.7f);
            shotsFired++;
        }
        if (stateTime > total * interval + 0.4) setState(State.HOVER);
    }

    private void dive(double dt) {
        double speed = phase == 3 ? 380 : 320;
        double dx = targetX - cx(), dy = targetY - bottom();
        double d = Math.hypot(dx, dy);
        if (stateTime % 0.03 < dt) {
            level.particles.emit(cx(), cy(), 3, Particles.burst().speed(10, 40).life(0.2, 0.4).colors(0x3EC6E8, 0x1A1420));
        }
        if (d < speed * dt || bottom() >= floorY - 1 || stateTime > 1.6) {
            y = floorY - h;
            setState(State.DIVE_REST);
            Audio.get().play("sounds/player/crouch", 1f, 0.6f);
            level.camera.shake(0.5);
            level.particles.emit(cx(), floorY, 24, Particles.burst().speed(40, 140).direction(-90, 170).gravity(300)
                .life(0.3, 0.7).colors(0x8F8A9E, 0x6E6680, 0x4E4860).size(1, 3));
            level.banner(null, "STAGGERED - STRIKE NOW");
            return;
        }
        x += dx / d * speed * dt;
        y += dy / d * speed * dt;
        x = clampX(cx()) - w / 2;
    }

    private void breath(double dt) {
        y = floorY - h - 2;
        int frame = (int) (stateTime / BREATH_FRAME);
        if (frame >= 7 && frame <= 15 && stateTime % 0.03 < dt) {
            double[] box = breathBox();
            if (box != null) {
                level.particles.emit(box[0] + Math.random() * box[2], box[1] + 20 + Math.random() * (box[3] - 20), 2,
                    Particles.burst().speed(20, 70).direction(facing > 0 ? 0 : 180, 40).life(0.2, 0.5)
                        .colors(0x3EC6E8, 0x9CF6FF, 0xFFFFFF));
            }
            if (frame == 8) level.camera.shake(0.05);
        }
        if (stateTime >= BREATH_FRAME * ATTACK.frames()) setState(State.HOVER);
    }

    private void summon() {
        Player p = player();
        int spawned = 0;
        for (int attempt = 0; attempt < 12 && spawned < 2; attempt++) {
            double sx = arenaLeft + 30 + Math.random() * (arenaRight - arenaLeft - 60);
            if (Math.abs(sx - p.cx()) < 70) continue;
            level.spawn(Skeleton.summoned(sx, floorY));
            spawned++;
        }
        Audio.get().play("sounds/demon/demon-idle", 0.6f, 1.3f);
    }

    private void rain(double dt) {
        bob(dt);
        int count = phase == 3 ? 7 : 5;
        if (rainMarks.isEmpty() && stateTime < dt * 1.5) {
            Player p = player();
            rainMarks.add(new double[]{p.cx(), 0});
            for (int i = 1; i < count; i++) {
                rainMarks.add(new double[]{arenaLeft + 24 + Math.random() * (arenaRight - arenaLeft - 48), i * 0.08});
            }
            sparkHands(0xFF2840);
            Audio.get().play("sounds/misc/fire-trap", 0.7f, 0.7f);
        }
        double fallAt = 0.9;
        for (double[] m : rainMarks) {
            if (stateTime - m[1] >= fallAt && stateTime - m[1] - dt < fallAt) {
                level.spawn(new Fireball(m[0], ceilingY + 6, 0, 280, 15));
            }
        }
        if (stateTime > fallAt + 0.8) {
            rainMarks.clear();
            setState(State.HOVER);
        }
    }

    private void dying(double dt) {
        x += Math.sin(stateTime * 60) * 0.6;
        if (stateTime % 0.12 < dt) {
            level.particles.emit(x + Math.random() * w, y + Math.random() * h, 16, Particles.burst().speed(40, 160)
                .life(0.3, 0.8).colors(0x3EC6E8, 0x9CF6FF, 0xFFFFFF, 0x1A1420).size(1, 3));
            Audio.get().playVaried("sounds/player/kill", 0.7f);
            level.camera.shake(0.25);
        }
        if (stateTime > 2.6) {
            removed = true;
            level.particles.emit(cx(), cy(), 120, Particles.burst().speed(60, 320).drag(1.5).life(0.5, 1.6)
                .colors(0x3EC6E8, 0x9CF6FF, 0xFFFFFF, 0x1A1420).size(1, 4));
            level.flash(0xFFFFFF, 0.5);
            level.onBossDefeated(this);
        }
    }

    // ------------------------------------------------------------------ helpers

    private void setState(State s) {
        state = s;
        stateTime = 0;
        vx = vy = 0;
    }

    private void bob(double dt) {
        y += Math.sin(time * 2.2) * 8 * dt;
    }

    /** Glides toward a point (by its feet); returns true once there. */
    private boolean flyTowards(double tx, double footY, double speed, double dt) {
        double dx = tx - cx(), dy = footY - bottom();
        double d = Math.hypot(dx, dy);
        if (d < 2) return true;
        double step = Math.min(d, speed * dt * Math.min(1, d / 30 + 0.3));
        x += dx / d * step;
        y += dy / d * step;
        return d < 6;
    }

    private double clampX(double cx) {
        return Math.max(arenaLeft + 30, Math.min(arenaRight - 30, cx));
    }

    private void sparkHands(int color) {
        for (int side = -1; side <= 1; side += 2) {
            level.particles.emit(cx() + side * 22, cy() + 4, 1, Particles.burst().speed(10, 40).gravity(-40)
                .life(0.2, 0.5).colors(color, 0xFFFFFF));
        }
    }

    // ------------------------------------------------------------------ drawing

    @Override
    public void render(Graphics2D g) {
        boolean right = facing > 0;
        boolean hit = flash > 0 || (state == State.DYING && (int) (stateTime * 12) % 2 == 0);
        float alpha = state == State.DYING ? (float) Math.max(0.2, 1 - stateTime / 2.6) : 1f;
        double fx = cx(), fy = bottom();
        switch (state) {
            case BREATH -> ATTACK.draw(g, ATTACK.frameAt(stateTime, BREATH_FRAME, false), fx, fy, right, hit, alpha);
            case DIVE -> ATTACK.draw(g, 2, fx, fy, right, hit, alpha);
            case DIVE_RISE -> ATTACK.draw(g, (int) (stateTime * 8) % 2 == 0 ? 0 : 1, fx, fy, right, true, 0.4f + (float) (stateTime % 0.2) * 3);
            case DIVE_REST -> IDLE.draw(g, 3, fx, fy + 2, right, hit, alpha);
            default -> IDLE.draw(g, IDLE.frameAt(time, state == State.ENRAGE ? 0.08 : 0.17, true), fx, fy, right, hit, alpha);
        }
        if (state == State.DIVE_RISE) IDLE.draw(g, 0, fx, fy, right, false, 0.6f);
        for (double[] m : rainMarks) {
            double t = stateTime - m[1];
            if (t < 0) continue;
            int r = 3 + (int) (t * 6) % 3;
            g.setColor(new Color(255, 80, 40, 200));
            g.fillRect((int) m[0] - r, (int) floorY - 2, r * 2, 2);
            g.setColor(new Color(255, 220, 120, 160));
            g.fillRect((int) m[0] - 1, (int) floorY - 4, 2, 2);
        }
    }

    @Override
    public void emitLight(Lighting lighting, int camX, int camY) {
        if (state == State.DORMANT) {
            lighting.light(cx() - camX, y + 10 - camY, 40, 0.5f, 0x3EC6E8);
            return;
        }
        int tint = phase == 3 ? 0xFF2840 : 0x3EC6E8;
        lighting.light(cx() - camX, y + 10 - camY, 90, 0.8f, tint);
        double[] box = breathBox();
        if (box != null) lighting.light(box[0] + box[2] / 2 - camX, box[1] + box[3] / 2 - camY, 120, 1f, 0x3EC6E8);
        for (double[] m : rainMarks) lighting.light(m[0] - camX, floorY - camY, 20, 0.8f, 0xFF5028);
    }

    @Override
    public int layer() {
        return 18;
    }
}
