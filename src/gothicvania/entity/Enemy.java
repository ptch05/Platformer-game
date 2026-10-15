package gothicvania.entity;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Particles;
import gothicvania.engine.Sprite;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Shared behaviour for everything that fights the hero: health, getting hit
 * (once per swing, with flash and knockback), contact damage and dying.
 */
public abstract class Enemy extends Entity {
    protected static final Sprite DEATH_PUFF = Assets.sprite("sprites/fx/enemy-death.png", 44, 22, 52, false);
    protected static final double GRAVITY = 1100;

    protected int hp;
    protected final int maxHp;
    protected int contactDamage;
    protected int souls;
    protected double flash;
    protected double stun;
    protected double time;
    private int lastSwing = -1;

    protected Enemy(double x, double y, double w, double h, int hp, int contactDamage, int souls) {
        super(x, y, w, h);
        this.hp = hp;
        this.maxHp = hp;
        this.contactDamage = contactDamage;
        this.souls = souls;
    }

    /** Whether touching it hurts right now (e.g. not while still rising from a grave). */
    public boolean isHarmful() {
        return hp > 0;
    }

    /** Whether it can currently be struck. */
    public boolean isVulnerable() {
        return hp > 0;
    }

    public int contactDamage() {
        return contactDamage;
    }

    public int hp() {
        return hp;
    }

    public int maxHp() {
        return maxHp;
    }

    /**
     * Takes a hit from the hero's sword. Each swing can only land once.
     *
     * @return true if the hit registered
     */
    public boolean strike(int swingId, int damage, double fromX) {
        if (swingId == lastSwing || !isVulnerable()) return false;
        lastSwing = swingId;
        return damage(damage, fromX);
    }

    /** Raw damage (reflected fireballs, specials). */
    public boolean damage(int amount, double fromX) {
        if (!isVulnerable()) return false;
        hp -= amount;
        flash = 0.12;
        int dir = cx() < fromX ? -1 : 1;
        onHurt(dir);
        level.particles.emit(cx(), cy(), 8, Particles.burst().speed(40, 120).direction(dir > 0 ? 0 : 180, 100)
            .gravity(300).life(0.2, 0.45).colors(bloodColors()).size(1, 2));
        if (hp <= 0) {
            hp = 0;
            die();
        } else {
            Audio.get().playVaried("sounds/player/kill", 0.45f);
        }
        return true;
    }

    /** Reaction to a non-lethal hit; default is a small knockback and stagger. */
    protected void onHurt(int dir) {
        vx = dir * 110;
        vy = Math.min(vy, -70);
        stun = 0.25;
    }

    protected void die() {
        removed = true;
        Audio.get().playVaried("sounds/player/kill", 1f);
        level.spawn(new Effect(DEATH_PUFF, 0.08, cx(), bottom(), facing > 0));
        level.particles.emit(cx(), cy(), 18, Particles.burst().speed(30, 150).gravity(260).life(0.3, 0.8)
            .colors(bloodColors()).size(1, 3));
        level.onEnemyKilled(this, souls);
    }

    protected int[] bloodColors() {
        return new int[]{0xD8C8A8, 0x9C8C70, 0x6E2030};
    }

    protected Player player() {
        return level.player();
    }

    /** Horizontal direction to the player (-1 or 1). */
    protected int dirToPlayer() {
        return player().cx() < cx() ? -1 : 1;
    }

    protected boolean playerWithin(double dx, double dy) {
        Player p = player();
        return !p.isDead() && Math.abs(p.cx() - cx()) < dx && Math.abs(p.bottom() - bottom()) < dy;
    }

    protected void applyGravity(double dt) {
        vy = Math.min(480, vy + GRAVITY * dt);
    }

    @Override
    public void update(double dt) {
        time += dt;
        flash = Math.max(0, flash - dt);
        stun = Math.max(0, stun - dt);
        think(dt);
    }

    protected abstract void think(double dt);

    @Override
    public void renderOverlay(Graphics2D g) {
        if (hp < maxHp && hp > 0 && maxHp > 1 && !(this instanceof Demon)) {
            int bw = 16, bx = (int) (cx() - bw / 2.0), by = (int) (y - 6);
            g.setColor(new Color(10, 4, 12, 200));
            g.fillRect(bx - 1, by - 1, bw + 2, 4);
            g.setColor(new Color(200, 30, 50));
            g.fillRect(bx, by, (int) Math.ceil(bw * hp / (double) maxHp), 2);
        }
    }
}
