package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Lighting;
import gothicvania.engine.Particles;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Things to collect. Souls drift toward the hero once close; the rest bob in
 * place until touched.
 */
public final class Pickup extends Entity {
    public enum Kind {
        /** Restores health. */
        POTION("sprites/items/potion.png", 0xFF3A4A),
        /** Halves damage taken for a while. */
        ARMOUR("sprites/items/armour.png", 0x6FA8FF),
        /** Score; also dropped by enemies. */
        SOUL(null, 0x6FE8FF),
        /** Hidden in secret areas: raises maximum health for the rest of the run. */
        HEART("sprites/items/heart.png", 0xFF2840);

        final String image;
        final int glow;

        Kind(String image, int glow) {
            this.image = image;
            this.glow = glow;
        }
    }

    public final Kind kind;
    /** Identifies placed pickups so they stay collected after a respawn; -1 for drops. */
    public final int id;
    private final BufferedImage image;
    private double time = Math.random() * 6;
    private double homeY;
    private boolean magnet;

    public Pickup(Kind kind, double cx, double cy, int id) {
        super(cx - 5, cy - 5, 10, 10);
        this.kind = kind;
        this.id = id;
        this.homeY = y;
        this.image = kind.image == null ? null : Assets.image(kind.image);
    }

    /** A soul flung out of a defeated enemy. */
    public static Pickup droppedSoul(double cx, double cy) {
        Pickup p = new Pickup(Kind.SOUL, cx, cy, -1);
        p.vx = (Math.random() - 0.5) * 140;
        p.vy = -60 - Math.random() * 80;
        return p;
    }

    @Override
    public void update(double dt) {
        time += dt;
        Player player = level.player();
        if (kind == Kind.SOUL) {
            if (!magnet && distanceTo(player) < 56 && !player.isDead()) magnet = true;
            if (magnet) {
                double dx = player.cx() - cx(), dy = player.cy() - cy(), d = Math.max(1, Math.hypot(dx, dy));
                double speed = 160 + time * 60;
                vx += (dx / d * speed - vx) * Math.min(1, dt * 8);
                vy += (dy / d * speed - vy) * Math.min(1, dt * 8);
                x += vx * dt;
                y += vy * dt;
            } else if (vx != 0 || vy != 0) {
                vx *= Math.pow(0.1, dt);
                vy *= Math.pow(0.1, dt);
                x += vx * dt;
                y += vy * dt;
                homeY = y;
            } else {
                y = homeY + Math.sin(time * 3) * 2;
            }
        } else {
            y = homeY + Math.sin(time * 2.5) * 2;
        }
        if (overlaps(player) && !player.isDead()) collect(player);
    }

    private void collect(Player player) {
        removed = true;
        level.onPickup(this);
        switch (kind) {
            case POTION -> {
                player.heal(35);
                Audio.get().play("sounds/collectibles/potion", 0.8f, 1f);
                level.banner(null, "+35 HEALTH");
            }
            case ARMOUR -> {
                player.giveArmour();
                Audio.get().play("sounds/collectibles/armour", 0.8f, 1f);
                level.banner(null, "ARMOUR: DAMAGE HALVED");
            }
            case HEART -> {
                player.maxHp += 20;
                player.hp = player.maxHp;
                Audio.get().play("sounds/player/gain-special", 1f, 0.8f);
                level.banner("HEART OF THE FALLEN", "MAX HEALTH +20");
                level.flash(0xFF2840, 0.25);
            }
            case SOUL -> Audio.get().play("sounds/collectibles/potion", 0.25f, 1.6f + (float) Math.random() * 0.3f);
        }
        level.particles.emit(cx(), cy(), kind == Kind.SOUL ? 6 : 20, Particles.burst().speed(20, 90).life(0.3, 0.7)
            .colors(kind.glow, 0xFFFFFF).gravity(-40));
    }

    @Override
    public void render(Graphics2D g) {
        if (kind == Kind.SOUL) {
            int px = (int) Math.round(cx()), py = (int) Math.round(cy());
            int flicker = (int) (time * 10) % 2;
            g.setColor(new Color(0x3EC6E8));
            g.fillRect(px - 2, py - 2, 5, 5);
            g.setColor(new Color(0x9CF6FF));
            g.fillRect(px - 1, py - 3 - flicker, 3, 5);
            g.setColor(Color.WHITE);
            g.fillRect(px, py - 1, 1, 2);
            return;
        }
        g.drawImage(image, (int) Math.round(cx() - image.getWidth() / 2.0), (int) Math.round(bottom() - image.getHeight()), null);
        if ((int) (time * 2) % 4 == 0) {
            g.setColor(new Color(255, 255, 255, 180));
            g.fillRect((int) cx() + 2, (int) (bottom() - image.getHeight() + 3 + (time * 8) % 6), 1, 2);
        }
    }

    @Override
    public void emitLight(Lighting lighting, int camX, int camY) {
        lighting.light(cx() - camX, cy() - camY, kind == Kind.SOUL ? 18 : 34, 0.7f, kind.glow);
    }

    @Override
    public int layer() {
        return 25;
    }
}
