package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Particles;
import gothicvania.engine.Sprite;
import java.awt.Graphics2D;

/**
 * Lies in wait under the soil and claws its way out when the hero comes close,
 * then shambles along its ledge, lunging faster once it spots its prey. The
 * bare variant is already up and about, quicker and tougher.
 */
public final class Skeleton extends Enemy {
    private static final Sprite WALK = Assets.sprite("sprites/skeleton/walk.png", 44, 22, 52, false);
    private static final Sprite RISE = Assets.sprite("sprites/skeleton/rise.png", 44, 22, 52, false);
    private static final Sprite WALK_BARE = Assets.sprite("sprites/skeleton/walk-bare.png", 44, 22, 52, false);
    private static final Sprite RISE_BARE = Assets.sprite("sprites/skeleton/rise-bare.png", 44, 22, 52, false);
    private static final double RISE_FRAME = 0.11;

    private enum State { BURIED, RISING, WALK }

    private final boolean bare;
    private final double patrolSpeed, chaseSpeed;
    private State state;
    private double stateTime;

    /**
     * @param footX, footY where it stands
     * @param bare   the faster, already-awake variant
     * @param buried whether it starts underground
     */
    public Skeleton(double footX, double footY, boolean bare, boolean buried) {
        super(footX - 7, footY - 36, 14, 36, bare ? 3 : 2, 15, bare ? 3 : 2);
        this.bare = bare;
        this.patrolSpeed = bare ? 38 : 26;
        this.chaseSpeed = bare ? 78 : 56;
        this.state = buried ? State.BURIED : State.WALK;
        facing = -1;
    }

    /** Used by the demon to summon reinforcements that rise immediately. */
    public static Skeleton summoned(double footX, double footY) {
        Skeleton s = new Skeleton(footX, footY, true, true);
        s.state = State.RISING;
        return s;
    }

    @Override
    public boolean isHarmful() {
        return super.isHarmful() && (state == State.WALK || (state == State.RISING && stateTime > RISE_FRAME * 4));
    }

    @Override
    public boolean isVulnerable() {
        return super.isVulnerable() && state != State.BURIED;
    }

    @Override
    protected void think(double dt) {
        stateTime += dt;
        switch (state) {
            case BURIED -> {
                if (playerWithin(110, 70)) {
                    state = State.RISING;
                    stateTime = 0;
                    facing = dirToPlayer();
                    Audio.get().play("sounds/player/crouch", 0.5f, 0.7f);
                }
            }
            case RISING -> {
                if (stateTime % 0.1 < dt) {
                    level.particles.emit(cx(), bottom(), 3, Particles.burst().speed(20, 60).direction(-90, 120)
                        .gravity(300).life(0.3, 0.6).colors(0x4A3424, 0x6E4E36, 0x2C1E16));
                }
                if (stateTime >= RISE_FRAME * RISE.frames()) {
                    state = State.WALK;
                    stateTime = 0;
                }
            }
            case WALK -> walk(dt);
        }
        applyGravity(dt);
        move(dt);
    }

    private void walk(double dt) {
        if (stun > 0) {
            vx *= Math.pow(0.02, dt);
            return;
        }
        boolean chasing = playerWithin(150, 40);
        if (chasing) facing = dirToPlayer();
        double speed = chasing ? chaseSpeed : patrolSpeed;
        if (onGround && (!groundAhead(facing) || wallAhead(facing))) {
            if (chasing) {
                vx = 0; // wait at the edge rather than fall off
                return;
            }
            facing = -facing;
        }
        vx = facing * speed;
    }

    @Override
    public void render(Graphics2D g) {
        boolean right = facing > 0;
        switch (state) {
            case BURIED -> (bare ? RISE_BARE : RISE).draw(g, 0, cx(), bottom() + 1, right);
            case RISING -> (bare ? RISE_BARE : RISE).draw(g, RISE.frameAt(stateTime, RISE_FRAME, false), cx(), bottom() + 1, right, flash > 0, 1f);
            case WALK -> {
                double frameTime = Math.abs(vx) > patrolSpeed + 5 ? 0.075 : 0.12;
                (bare ? WALK_BARE : WALK).draw(g, WALK.frameAt(time, frameTime, true), cx(), bottom() + 1, right, flash > 0, 1f);
            }
        }
    }
}
