package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Input;
import gothicvania.engine.Input.Action;
import gothicvania.engine.Lighting;
import gothicvania.engine.Particles;
import gothicvania.engine.Sprite;
import gothicvania.world.Tile;
import gothicvania.world.TileMap;
import java.awt.Graphics2D;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The hero. Movement is tuned for responsiveness: coyote time and jump buffering
 * forgive late or early presses, releasing jump early cuts the jump short, and
 * the dash and wall-jump open up the level design. Combat has a sword swing, a
 * screen-clearing "Bloodthirst" special that charges as you fight, and
 * invulnerability frames after being hit or while dashing.
 */
public final class Player extends Entity {
    // movement tuning (pixels, seconds)
    private static final double RUN_SPEED = 125;
    private static final double GROUND_ACCEL = 1500, GROUND_DECEL = 1900;
    private static final double AIR_ACCEL = 1050, AIR_DECEL = 520;
    private static final double GRAVITY = 1150, MAX_FALL = 470;
    private static final double JUMP_SPEED = 400, JUMP_CUT = 0.45;
    private static final double COYOTE_TIME = 0.10, JUMP_BUFFER = 0.13;
    private static final double DASH_SPEED = 300, DASH_TIME = 0.17, DASH_COOLDOWN = 0.42;
    private static final double WALL_SLIDE_SPEED = 65, WALL_JUMP_X = 170, WALL_JUMP_Y = 370, WALL_JUMP_LOCK = 0.16;
    private static final double STAND_HEIGHT = 36, CROUCH_HEIGHT = 22, WIDTH = 14;

    // combat tuning
    private static final double ATTACK_FRAME = 0.05, ATTACK_BUFFER = 0.12;
    private static final double SPECIAL_FRAME = 0.055;
    private static final double HURT_TIME = 0.28, INVULNERABLE_TIME = 1.1;
    public static final double BLOODTHIRST_MAX = 100;
    private static final double ARMOUR_TIME = 20;

    private static final Sprite IDLE = hero("idle"), RUN = hero("run"), JUMP = hero("jump"), ATTACK = hero("attack");
    private static final Sprite CROUCH = hero("crouch"), HURT = hero("hurt"), DEATH = hero("death"), SPECIAL = hero("special");

    private static Sprite hero(String name) {
        return Assets.sprite("sprites/hero/" + name + ".png", 160, 80, 90, true);
    }

    public enum State { NORMAL, CROUCH, ATTACK, SPECIAL, DASH, HURT, DEAD }

    public int hp;
    public int maxHp;
    public double bloodthirst;
    public double armourTime;
    public boolean god;
    private State state = State.NORMAL;
    private double stateTime;
    private double animTime;
    private double coyote, jumpBuffer, attackBuffer, dashCooldown, invulnerable, wallJumpLock, dropThrough;
    private boolean airDashUsed;
    private int wallSlide;
    private int swing;
    private double landSquash;
    private double dustTimer;
    private double fallSpeed;
    private double safeX, safeY;
    private final Deque<double[]> afterimages = new ArrayDeque<>();

    public Player(double footX, double footY, int maxHp) {
        super(footX - WIDTH / 2, footY - STAND_HEIGHT, WIDTH, STAND_HEIGHT);
        this.maxHp = maxHp;
        this.hp = maxHp;
        safeX = x;
        safeY = y;
    }

    // ------------------------------------------------------------------ queries used by the level

    public State state() {
        return state;
    }

    public boolean isDead() {
        return state == State.DEAD;
    }

    public boolean isInvulnerable() {
        return god || invulnerable > 0 || state == State.DASH || state == State.SPECIAL || state == State.DEAD;
    }

    public boolean armoured() {
        return armourTime > 0;
    }

    /** Identifies the current swing so an enemy is only hit once per swing. */
    public int swingId() {
        return swing;
    }

    /** The sword's hitbox {x, y, w, h} while it is dangerous, else null. */
    public double[] attackBox() {
        if (state == State.ATTACK) {
            int frame = (int) (stateTime / ATTACK_FRAME);
            if (frame < 2 || frame > 4) return null;
            double reach = 44;
            return new double[]{facing > 0 ? cx() - 2 : cx() - reach + 2, bottom() - 36, reach, 28};
        }
        if (state == State.SPECIAL) {
            int frame = (int) (stateTime / SPECIAL_FRAME);
            if (frame < 5 || frame > 8) return null;
            return new double[]{cx() - 68, bottom() - 76, 136, 82};
        }
        return null;
    }

    public int damageMultiplier() {
        return state == State.SPECIAL ? 5 : 1;
    }

    public double safeX() {
        return safeX;
    }

    public double safeY() {
        return safeY;
    }

    // ------------------------------------------------------------------ events

    /** A swing connected with something. */
    public void onHitLanded(boolean killed) {
        if (state != State.SPECIAL) gainBloodthirst(killed ? 16 : 6);
    }

    public void gainBloodthirst(double amount) {
        boolean wasFull = bloodthirst >= BLOODTHIRST_MAX;
        bloodthirst = Math.min(BLOODTHIRST_MAX, bloodthirst + amount);
        if (!wasFull && bloodthirst >= BLOODTHIRST_MAX) {
            Audio.get().play("sounds/player/gain-special", 0.8f, 1f);
            level.banner("BLOODTHIRSTY", "PRESS G TO UNLEASH");
        }
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public void giveArmour() {
        armourTime = ARMOUR_TIME;
    }

    /**
     * Takes damage from something at {@code fromX}. Returns false if the hit was
     * ignored (invulnerable). Armour halves damage.
     */
    public boolean hurt(int damage, double fromX) {
        if (isInvulnerable()) return false;
        int dealt = armoured() ? Math.max(1, damage / 2) : damage;
        hp -= dealt;
        level.onPlayerDamaged(dealt);
        int away = cx() < fromX ? -1 : 1;
        standUp();
        if (hp <= 0) {
            hp = 0;
            die();
            return true;
        }
        setState(State.HURT);
        vx = away * 150;
        vy = -210;
        invulnerable = INVULNERABLE_TIME;
        Audio.get().playVaried("sounds/player/hurt", 1f);
        level.camera.shake(0.45);
        level.hitstop(0.07);
        level.particles.emit(cx(), cy(), 14, Particles.burst().speed(40, 140).direction(away > 0 ? 0 : 180, 120)
            .gravity(500).life(0.3, 0.6).colors(0xB0182A, 0x7A0E1C, 0xE23A4A).size(1, 3));
        return true;
    }

    /** Kills the hero outright (rising lava, falling out of the world with no health left). */
    public void die() {
        if (state == State.DEAD) return;
        hp = 0;
        setState(State.DEAD);
        vx = -facing * 60;
        vy = -160;
        Audio.get().play("sounds/player/hurt", 1f, 0.8f);
        level.camera.shake(0.7);
        level.slowMotion(0.35, 0.8);
        level.onPlayerDied();
    }

    /** Puts the hero back on safe ground after touching a hazard, with a short grace period. */
    public void respawnAtSafeGround() {
        x = safeX;
        y = safeY;
        vx = vy = 0;
        setState(State.NORMAL);
        invulnerable = INVULNERABLE_TIME;
    }

    public void placeAt(double footX, double footY) {
        x = footX - w / 2;
        y = footY - h;
        safeX = x;
        safeY = y;
        vx = vy = 0;
    }

    // ------------------------------------------------------------------ update

    @Override
    public void update(double dt) {
        Input in = level.input();
        stateTime += dt;
        animTime += dt;
        dashCooldown -= dt;
        invulnerable -= dt;
        wallJumpLock -= dt;
        dropThrough -= dt;
        armourTime = Math.max(0, armourTime - dt);
        landSquash = Math.max(0, landSquash - dt * 6);
        fadeAfterimages(dt);

        if (state == State.DEAD) {
            vx *= Math.pow(0.05, dt);
            vy = Math.min(MAX_FALL, vy + GRAVITY * dt);
            move(dt);
            return;
        }

        if (in.pressed(Action.JUMP)) jumpBuffer = JUMP_BUFFER;
        else jumpBuffer -= dt;
        if (in.pressed(Action.ATTACK)) attackBuffer = ATTACK_BUFFER;
        else attackBuffer -= dt;
        if (onGround) {
            coyote = COYOTE_TIME;
            airDashUsed = false;
        } else {
            coyote -= dt;
        }

        switch (state) {
            case NORMAL -> updateNormal(in, dt);
            case CROUCH -> updateCrouch(in, dt);
            case ATTACK -> updateAttack(in, dt);
            case SPECIAL -> updateSpecial(dt);
            case DASH -> updateDash(dt);
            case HURT -> {
                applyGravity(in, dt);
                vx = approach(vx, 0, AIR_DECEL * dt);
                if (stateTime > HURT_TIME) setState(State.NORMAL);
            }
            default -> {
            }
        }

        boolean wasOnGround = onGround;
        double preVy = vy;
        move(dt);
        if (onGround && !wasOnGround) land(preVy);
        if (!onGround) fallSpeed = Math.max(fallSpeed, vy);

        trackSafeGround();
        checkHazards();
    }

    private void updateNormal(Input in, double dt) {
        int ax = wallJumpLock > 0 ? 0 : in.axisX();
        if (ax != 0) facing = ax;

        if (tryStartAction(in)) return;
        if (onGround && in.down(Action.DOWN) && ax == 0 && jumpBuffer <= 0) {
            crouch();
            return;
        }

        double target = ax * RUN_SPEED;
        double accel = onGround ? (ax != 0 ? GROUND_ACCEL : GROUND_DECEL) : (ax != 0 ? AIR_ACCEL : AIR_DECEL);
        if (wallJumpLock <= 0) vx = approach(vx, target, accel * dt);

        // wall slide: falling while pushing into a wall
        wallSlide = 0;
        if (!onGround && vy > 0 && ax != 0 && touchingWall(ax)) {
            wallSlide = ax;
            vy = Math.min(vy, WALL_SLIDE_SPEED);
            dustTimer -= dt;
            if (dustTimer <= 0) {
                dustTimer = 0.07;
                level.particles.emit(ax > 0 ? right() : x, y + 8, 1, Particles.burst().speed(5, 20).direction(-90, 60)
                    .life(0.2, 0.4).colors(0x8F8A9E, 0x5F5A6E));
            }
        }

        if (jumpBuffer > 0) {
            if (coyote > 0) {
                jump();
            } else {
                int wall = touchingWall(1) ? 1 : touchingWall(-1) ? -1 : 0;
                if (wall != 0) wallJump(wall);
            }
        }
        if (in.released(Action.JUMP) && vy < 0) vy *= JUMP_CUT;
        applyGravity(in, dt);

        if (onGround && Math.abs(vx) > 60) {
            dustTimer -= dt;
            if (dustTimer <= 0) {
                dustTimer = 0.16;
                level.particles.emit(cx() - facing * 4, bottom() - 1, 1, Particles.burst().speed(8, 24)
                    .direction(facing > 0 ? 200 : -20, 40).life(0.2, 0.35).colors(0x6E6680, 0x4E4860));
            }
        }
    }

    private boolean tryStartAction(Input in) {
        if (in.pressed(Action.SPECIAL) && bloodthirst >= BLOODTHIRST_MAX) {
            startSpecial();
            return true;
        }
        if (in.pressed(Action.DASH) && dashCooldown <= 0 && (onGround || !airDashUsed)) {
            startDash(in.axisX() != 0 ? in.axisX() : facing);
            return true;
        }
        if (attackBuffer > 0) {
            startAttack();
            return true;
        }
        return false;
    }

    private void updateCrouch(Input in, double dt) {
        vx = approach(vx, 0, GROUND_DECEL * dt);
        applyGravity(in, dt);
        if (in.axisX() != 0) facing = in.axisX();
        if (jumpBuffer > 0 && onLedge()) {
            // drop down through a one-way ledge
            jumpBuffer = 0;
            dropThrough = 0.25;
            standUp();
            setState(State.NORMAL);
            y += 1;
            return;
        }
        boolean wantsUp = !in.down(Action.DOWN) || !onGround || in.axisX() != 0 || jumpBuffer > 0
            || attackBuffer > 0 || in.pressed(Action.DASH) || in.pressed(Action.SPECIAL);
        if (wantsUp && standUp()) {
            setState(State.NORMAL);
            updateNormal(in, 0);
        }
    }

    private void updateAttack(Input in, double dt) {
        int ax = in.axisX();
        if (onGround) {
            vx = approach(vx, 0, GROUND_DECEL * 0.8 * dt);
        } else {
            vx = approach(vx, ax * RUN_SPEED * 0.8, AIR_ACCEL * 0.6 * dt);
        }
        applyGravity(in, dt);
        int frame = (int) (stateTime / ATTACK_FRAME);
        // allow cancelling the recovery into movement so combat stays fluid
        if (frame >= 4 && in.pressed(Action.DASH) && dashCooldown <= 0 && (onGround || !airDashUsed)) {
            startDash(ax != 0 ? ax : facing);
            return;
        }
        if (frame >= 4 && jumpBuffer > 0 && coyote > 0) {
            setState(State.NORMAL);
            jump();
            return;
        }
        if (stateTime >= ATTACK_FRAME * ATTACK.frames()) {
            setState(State.NORMAL);
            if (attackBuffer > 0) startAttack();
        }
    }

    private void updateSpecial(double dt) {
        vx = approach(vx, 0, GROUND_DECEL * dt);
        vy = 0;
        int frame = (int) (stateTime / SPECIAL_FRAME);
        if (frame == 5 && stateTime - dt < 5 * SPECIAL_FRAME) {
            level.camera.shake(0.6);
            level.flash(0xFFFFFF, 0.12);
            level.particles.emit(cx(), bottom() - 30, 50, Particles.burst().speed(80, 260).life(0.3, 0.7)
                .drag(3).colors(0x9CF6FF, 0x3EC6E8, 0xFFFFFF).size(1, 3));
        }
        if (stateTime >= SPECIAL_FRAME * SPECIAL.frames()) {
            setState(State.NORMAL);
            invulnerable = 0.3;
        }
    }

    private void updateDash(double dt) {
        vx = facing * DASH_SPEED;
        vy = 0;
        if (stateTime % 0.03 < dt) afterimages.add(new double[]{cx(), bottom(), facing, 0.25});
        if (stateTime >= DASH_TIME || wallContact != 0) {
            vx = facing * RUN_SPEED;
            setState(State.NORMAL);
        }
    }

    private void applyGravity(Input in, double dt) {
        double g = GRAVITY;
        if (Math.abs(vy) < 55 && in.down(Action.JUMP) && state == State.NORMAL) g *= 0.55; // floaty apex
        vy = Math.min(MAX_FALL, vy + g * dt);
    }

    // ------------------------------------------------------------------ actions

    private void jump() {
        vy = -JUMP_SPEED;
        jumpBuffer = 0;
        coyote = 0;
        onGround = false;
        Audio.get().playVaried("sounds/player/jump", 0.7f);
        level.particles.emit(cx(), bottom(), 6, Particles.burst().speed(15, 45).direction(-90, 160)
            .life(0.2, 0.4).colors(0x8F8A9E, 0x6E6680));
    }

    private void wallJump(int wallSide) {
        vx = -wallSide * WALL_JUMP_X;
        vy = -WALL_JUMP_Y;
        facing = -wallSide;
        wallJumpLock = WALL_JUMP_LOCK;
        jumpBuffer = 0;
        airDashUsed = false;
        Audio.get().playVaried("sounds/player/jump", 0.7f);
        level.particles.emit(wallSide > 0 ? right() : x, cy(), 8, Particles.burst().speed(20, 60)
            .direction(wallSide > 0 ? 180 : 0, 90).life(0.2, 0.4).colors(0x8F8A9E, 0x6E6680));
    }

    private void startAttack() {
        swing++;
        attackBuffer = 0;
        setState(State.ATTACK);
        Audio.get().playVaried("sounds/player/attack", 0.9f);
        if (onGround) vx *= 0.3;
    }

    private void startSpecial() {
        swing++;
        bloodthirst = 0;
        setState(State.SPECIAL);
        vx = 0;
        Audio.get().play("sounds/player/special-attack", 1f, 1f);
        level.slowMotion(0.5, 0.18);
    }

    private void startDash(int dir) {
        facing = dir;
        setState(State.DASH);
        dashCooldown = DASH_COOLDOWN;
        if (!onGround) airDashUsed = true;
        Audio.get().play("sounds/player/attack", 0.6f, 0.65f);
        level.particles.emit(cx(), bottom() - 4, 8, Particles.burst().speed(30, 80).direction(dir > 0 ? 180 : 0, 50)
            .life(0.2, 0.4).colors(0xB49BE0, 0x7A5CB8, 0xFFFFFF));
    }

    private void crouch() {
        setState(State.CROUCH);
        y += h - CROUCH_HEIGHT;
        h = CROUCH_HEIGHT;
    }

    /** Restores standing height if there's headroom. */
    private boolean standUp() {
        if (h == STAND_HEIGHT) return true;
        double newY = y - (STAND_HEIGHT - h);
        if (level.map.solidIn(x, newY, w, STAND_HEIGHT - h)) return false;
        y = newY;
        h = STAND_HEIGHT;
        return true;
    }

    private void land(double impactVy) {
        if (fallSpeed > 260) {
            landSquash = 1;
            level.particles.emit(cx(), bottom(), 8, Particles.burst().speed(20, 60).direction(-90, 170)
                .life(0.25, 0.45).colors(0x8F8A9E, 0x6E6680, 0x4E4860));
        }
        if (fallSpeed > 420) level.camera.shake(0.12);
        fallSpeed = 0;
    }

    private void setState(State s) {
        if (state == State.CROUCH && s != State.CROUCH) standUp();
        state = s;
        stateTime = 0;
        if (s != State.NORMAL) wallSlide = 0;
    }

    private boolean touchingWall(int dir) {
        double probe = dir > 0 ? right() + 1 : x - 2;
        return level.map.solidIn(probe, y + 4, 1, h - 10);
    }

    private boolean onLedge() {
        int row = TileMap.tile(bottom() + 1);
        return level.map.get(TileMap.tile(x + 1), row) == Tile.PLATFORM || level.map.get(TileMap.tile(right() - 1), row) == Tile.PLATFORM;
    }

    @Override
    protected boolean droppingThrough() {
        return dropThrough > 0;
    }

    private void trackSafeGround() {
        if (!onGround || riding != null || state == State.HURT) return;
        int row = TileMap.tile(bottom() + 1);
        int left = TileMap.tile(x), right = TileMap.tile(right());
        if (!level.map.get(left, row).solid() || !level.map.get(right, row).solid()) return;
        if (level.map.hazardNear(TileMap.tile(cx()), TileMap.tile(bottom() - 1), 2)) return;
        safeX = x;
        safeY = bottom() - STAND_HEIGHT;
    }

    private void checkHazards() {
        Tile hazard = level.map.hazardIn(x, y, w, h);
        if (hazard != null) level.onPlayerHazard(hazard);
        else if (y > level.map.heightPx() + 24) level.onPlayerHazard(null);
    }

    private void fadeAfterimages(double dt) {
        for (double[] a : afterimages) a[3] -= dt;
        while (!afterimages.isEmpty() && afterimages.peekFirst()[3] <= 0) afterimages.removeFirst();
    }

    private static double approach(double value, double target, double amount) {
        return value < target ? Math.min(target, value + amount) : Math.max(target, value - amount);
    }

    // ------------------------------------------------------------------ rendering

    @Override
    public int layer() {
        return 20;
    }

    @Override
    public void render(Graphics2D g) {
        for (double[] a : afterimages) {
            RUN.draw(g, 3, a[0], a[1], a[2] > 0, true, (float) (a[3] / 0.25 * 0.35));
        }
        boolean blink = invulnerable > 0 && state != State.HURT && state != State.DEAD && (int) (invulnerable * 14) % 2 == 0;
        if (blink) return;
        boolean flash = state == State.HURT && stateTime < 0.08;
        boolean right = facing > 0;
        double fx = cx(), fy = bottom();
        switch (state) {
            case DEAD -> DEATH.draw(g, DEATH.frameAt(stateTime, 0.11, false), fx, fy, right);
            case SPECIAL -> SPECIAL.draw(g, SPECIAL.frameAt(stateTime, SPECIAL_FRAME, false), fx, fy, right);
            case ATTACK -> ATTACK.draw(g, ATTACK.frameAt(stateTime, ATTACK_FRAME, false), fx, fy, right);
            case DASH -> RUN.draw(g, 3, fx, fy, right, false, 1f);
            case HURT -> HURT.draw(g, 0, fx, fy, right, flash, 1f);
            case CROUCH -> CROUCH.draw(g, 0, fx, fy, right);
            default -> renderMoving(g, fx, fy, right);
        }
    }

    private void renderMoving(Graphics2D g, double fx, double fy, boolean right) {
        if (wallSlide != 0) {
            JUMP.draw(g, 2, fx, fy, wallSlide < 0);
        } else if (!onGround) {
            int frame = vy < -200 ? (stateTime < 0.06 ? 0 : 1) : vy < 120 ? 2 : 3;
            JUMP.draw(g, frame, fx, fy, right);
        } else if (Math.abs(vx) > 12) {
            RUN.draw(g, RUN.frameAt(animTime, 0.085, true), fx, fy, right);
        } else {
            IDLE.draw(g, IDLE.frameAt(animTime, 0.17, true), fx, fy + (landSquash > 0.5 ? 1 : 0), right);
        }
    }

    @Override
    public void emitLight(Lighting lighting, int camX, int camY) {
        lighting.light(cx() - camX, cy() - camY, 90, 0.85f, -1);
        if (armoured()) lighting.light(cx() - camX, cy() - camY, 40, 0.5f, 0x6FA8FF);
        if (bloodthirst >= BLOODTHIRST_MAX) lighting.light(cx() - camX, cy() - camY, 36, 0.5f, 0xFF2840);
    }
}
