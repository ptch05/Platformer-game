package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Particles;
import gothicvania.world.Theme;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** A rotten ledge: stand on it and it shakes, gives way, and grows back a few seconds later. */
public final class CrumbleBlock extends Platform {
    private static final double SHAKE_TIME = 0.45, GONE_TIME = 3.0;

    private enum State { SOLID, SHAKING, GONE }

    private final double homeY;
    private final BufferedImage tiles;
    private State state = State.SOLID;
    private double stateTime;
    private double fallY;

    public CrumbleBlock(double x, double y, Theme theme) {
        super(x, y, 16, 16);
        homeY = y;
        tiles = Assets.image(theme.tiles());
    }

    @Override
    public boolean isSolidTop() {
        return state != State.GONE;
    }

    @Override
    public void update(double dt) {
        stateTime += dt;
        switch (state) {
            case SOLID -> {
                if (level.player().riding == this) setState(State.SHAKING);
            }
            case SHAKING -> {
                if (stateTime > SHAKE_TIME) {
                    setState(State.GONE);
                    fallY = 0;
                    Audio.get().play("sounds/player/crouch", 0.5f, 1.3f);
                    level.particles.emit(cx(), cy(), 10, Particles.burst().speed(10, 50).gravity(400).life(0.4, 0.8)
                        .colors(0x4A3424, 0x6E4E36, 0x2C1E16).area(16, 8));
                }
            }
            case GONE -> {
                fallY += dt * 160;
                if (stateTime > GONE_TIME && !level.player().overlaps(x, homeY - 2, w, h + 2)) {
                    setState(State.SOLID);
                    level.particles.emit(cx(), cy(), 6, Particles.burst().speed(5, 20).life(0.3, 0.5)
                        .colors(0x8F8A9E, 0x6E6680).area(16, 16));
                }
            }
        }
    }

    private void setState(State s) {
        state = s;
        stateTime = 0;
    }

    @Override
    public void render(Graphics2D g) {
        int px = (int) x, py = (int) y;
        float alpha = 1f;
        if (state == State.SHAKING) px += (int) (Math.sin(stateTime * 90) * 1.5);
        if (state == State.GONE) {
            py += (int) fallY;
            alpha = (float) Math.max(0, 1 - stateTime * 2.5);
            if (stateTime > GONE_TIME - 0.4) {
                // ghostly preview just before it returns
                py = (int) homeY;
                alpha = 0.3f;
            }
        }
        if (alpha <= 0) return;
        Composite comp = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.drawImage(tiles, px, py, px + 16, py + 10, 16, 0, 32, 10, null);
        g.drawImage(tiles, px, py + 10, px + 16, py + 14, 16, 26, 32, 30, null);
        // weathered tint and cracks so it reads as unsafe on any tileset
        g.setColor(new Color(150, 90, 40, 70));
        g.fillRect(px, py, 16, 14);
        g.setColor(new Color(16, 8, 6));
        g.drawLine(px + 3, py + 2, px + 6, py + 7);
        g.drawLine(px + 6, py + 7, px + 5, py + 12);
        g.drawLine(px + 6, py + 7, px + 12, py + 9);
        g.drawLine(px + 12, py + 9, px + 13, py + 13);
        g.setColor(new Color(220, 180, 130, 160));
        g.fillRect(px + 7, py + 6, 1, 1);
        g.fillRect(px + 13, py + 8, 1, 1);
        g.setComposite(comp);
    }
}
