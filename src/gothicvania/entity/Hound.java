package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Particles;
import gothicvania.engine.Sprite;
import java.awt.Graphics2D;

/**
 * A hell-hound that prowls its ledge. When it spots the hero ahead it crouches,
 * shudders (your cue to react) and pounces in a long arc, then skids to a stop.
 */
public final class Hound extends Enemy {
    private static final Sprite RUN = Assets.sprite("sprites/hound/run.png", 96, 48, 53, false);

    private enum State { PROWL, CROUCH, POUNCE, SKID }

    private State state = State.PROWL;
    private double stateTime;
    private double cooldown;

    public Hound(double footX, double footY) {
        super(footX - 14, footY - 22, 28, 22, 3, 20, 3);
        facing = -1;
    }

    @Override
    protected void think(double dt) {
        stateTime += dt;
        cooldown -= dt;
        if (stun > 0 && state != State.POUNCE) {
            vx *= Math.pow(0.02, dt);
            applyGravity(dt);
            move(dt);
            return;
        }
        switch (state) {
            case PROWL -> {
                if (onGround && (!groundAhead(facing) || wallAhead(facing))) facing = -facing;
                vx = facing * 58;
                boolean ahead = (player().cx() - cx()) * facing > 0;
                if (cooldown <= 0 && onGround && playerWithin(150, 36) && ahead) {
                    setState(State.CROUCH);
                    vx = 0;
                }
            }
            case CROUCH -> {
                vx = 0;
                facing = dirToPlayer();
                if (stateTime > 0.38) {
                    setState(State.POUNCE);
                    double dist = Math.min(150, Math.abs(player().cx() - cx()) + 20);
                    vx = facing * Math.max(170, dist * 1.7);
                    vy = -250;
                    Audio.get().play("sounds/player/attack", 0.7f, 0.55f);
                }
            }
            case POUNCE -> {
                if (onGround && stateTime > 0.1) {
                    setState(State.SKID);
                    level.particles.emit(cx(), bottom(), 8, Particles.burst().speed(20, 70).direction(-90, 160)
                        .life(0.25, 0.5).colors(0x5E4A70, 0x3C2E4A));
                }
            }
            case SKID -> {
                vx *= Math.pow(0.004, dt);
                if (stateTime > 0.45) {
                    setState(State.PROWL);
                    cooldown = 1.1;
                    if (!groundAhead(facing)) facing = -facing;
                }
            }
        }
        applyGravity(dt);
        move(dt);
    }

    private void setState(State s) {
        state = s;
        stateTime = 0;
    }

    @Override
    protected void onHurt(int dir) {
        if (state == State.POUNCE) return; // committed to the leap
        super.onHurt(dir);
        if (state == State.CROUCH) setState(State.PROWL);
    }

    @Override
    public void render(Graphics2D g) {
        boolean right = facing > 0;
        int frame;
        double ox = 0;
        switch (state) {
            case CROUCH -> {
                frame = 1;
                ox = Math.sin(stateTime * 70) * 1.2; // shudder as a tell
            }
            case POUNCE -> frame = 3;
            case SKID -> frame = 2;
            default -> frame = RUN.frameAt(time, 0.1, true) % 3;
        }
        RUN.draw(g, frame, cx() + ox, bottom() + 1, right, flash > 0, 1f);
    }
}
