package gothicvania.game;

import gothicvania.engine.Assets;
import gothicvania.engine.Display;
import gothicvania.engine.PixelFont;
import gothicvania.entity.Demon;
import gothicvania.entity.Player;
import gothicvania.world.Level;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** Health, Bloodthirst, souls, the boss bar and the big centred announcements. */
public final class Hud {
    private static final Color FRAME = new Color(14, 8, 20, 220);
    private static final Color GOLD = new Color(232, 196, 120);
    private static final Color BLOOD = new Color(196, 24, 44);
    private static final Color SOUL = new Color(110, 232, 255);
    private static final BufferedImage HEART = Assets.image("sprites/items/heart.png");
    private static final BufferedImage ARMOUR = Assets.image("sprites/items/armour.png");

    private String bannerTitle, bannerSub;
    private double bannerTime;
    private double shownHp = -1, shownBossHp = -1;
    private double time;

    public void banner(String title, String subtitle) {
        bannerTitle = title;
        bannerSub = subtitle;
        bannerTime = title == null ? 1.8 : 2.6;
    }

    public void update(double dt, Level level) {
        time += dt;
        bannerTime -= dt;
        Player p = level.player();
        if (shownHp < 0 || shownHp < p.hp) shownHp = p.hp;
        shownHp += (p.hp - shownHp) * Math.min(1, dt * 3);
        Demon boss = level.boss();
        if (boss != null) {
            if (shownBossHp < 0 || shownBossHp < boss.hp()) shownBossHp = boss.hp();
            shownBossHp += (boss.hp() - shownBossHp) * Math.min(1, dt * 2.5);
        }
    }

    public void render(Graphics2D g, Level level) {
        Player p = level.player();
        int x = 8, y = 8;

        // health
        int barW = 70 + (p.maxHp - 100) / 2;
        g.drawImage(HEART, x, y, null);
        panel(g, x + 13, y, barW + 4, 9);
        g.setColor(new Color(90, 20, 30));
        g.fillRect(x + 15, y + 2, (int) (barW * shownHp / p.maxHp), 5);
        g.setColor(p.hp <= p.maxHp / 4 && (int) (time * 4) % 2 == 0 ? new Color(255, 90, 90) : BLOOD);
        g.fillRect(x + 15, y + 2, (int) (barW * p.hp / (double) p.maxHp), 5);
        g.setColor(new Color(255, 120, 130, 140));
        g.fillRect(x + 15, y + 2, (int) (barW * p.hp / (double) p.maxHp), 1);
        PixelFont.drawOutlined(g, String.valueOf(p.hp), x + barW + 22, y + 1, 1, Color.WHITE, Color.BLACK);

        // bloodthirst
        y += 12;
        boolean full = p.bloodthirst >= Player.BLOODTHIRST_MAX;
        panel(g, x + 13, y, 52, 6);
        g.setColor(full ? ((int) (time * 6) % 2 == 0 ? new Color(255, 40, 64) : new Color(255, 160, 170)) : new Color(140, 20, 40));
        g.fillRect(x + 15, y + 2, (int) (48 * p.bloodthirst / Player.BLOODTHIRST_MAX), 2);
        g.setColor(BLOOD);
        g.fillRect(x + 3, y + 1, 5, 5);
        g.setColor(new Color(255, 120, 130));
        g.fillRect(x + 4, y + 2, 2, 2);
        if (full) PixelFont.drawOutlined(g, "BLOODTHIRSTY [G]", x + 70, y, 1, new Color(255, 70, 90), Color.BLACK);

        // armour
        if (p.armoured()) {
            y += 10;
            g.drawImage(ARMOUR, x, y, 10, 13, null);
            panel(g, x + 13, y + 3, 42, 6);
            g.setColor(new Color(110, 168, 255));
            g.fillRect(x + 15, y + 5, (int) (38 * p.armourTime / 20.0), 2);
        }

        // souls and time
        String souls = String.valueOf(level.progress().souls);
        int sx = Display.WIDTH - 10 - PixelFont.width(souls, 1);
        g.setColor(SOUL);
        g.fillRect(sx - 9, 9, 4, 5);
        g.setColor(Color.WHITE);
        g.fillRect(sx - 8, 10, 1, 2);
        PixelFont.drawOutlined(g, souls, sx, 8, 1, SOUL, Color.BLACK);
        String clock = RunStats.clock(level.progress().time);
        PixelFont.drawOutlined(g, clock, Display.WIDTH - 10 - PixelFont.width(clock, 1), 18, 1, new Color(170, 160, 190), Color.BLACK);

        renderBoss(g, level);
        renderBanner(g);
    }

    private void renderBoss(Graphics2D g, Level level) {
        Demon boss = level.boss();
        if (!level.bossFightActive() || boss == null || !boss.isAwake()) return;
        int w = 170, x = (Display.WIDTH - w) / 2, y = 18;
        PixelFont.drawCentered(g, Demon.NAME, Display.WIDTH / 2, y - 10, 1, GOLD, Color.BLACK);
        panel(g, x - 2, y - 2, w + 4, 8);
        g.setColor(new Color(240, 240, 240));
        g.fillRect(x, y, (int) (w * shownBossHp / boss.maxHp()), 4);
        g.setColor(boss.phase() == 3 ? new Color(255, 40, 64) : new Color(62, 198, 232));
        g.fillRect(x, y, (int) (w * boss.hp() / (double) boss.maxHp()), 4);
        g.setColor(FRAME);
        g.fillRect(x + w / 3, y, 1, 4);
        g.fillRect(x + 2 * w / 3, y, 1, 4);
    }

    private void renderBanner(Graphics2D g) {
        if (bannerTime <= 0) return;
        float a = (float) Math.min(1, Math.min(bannerTime / 0.4, 1));
        Color main = new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), (int) (255 * a));
        Color sub = new Color(220, 210, 230, (int) (255 * a));
        Color outline = new Color(0, 0, 0, (int) (220 * a));
        int y = 52;
        if (bannerTitle != null) {
            PixelFont.drawCentered(g, bannerTitle, Display.WIDTH / 2, y, 2, main, outline);
            y += 20;
        }
        if (bannerSub != null) PixelFont.drawCentered(g, bannerSub, Display.WIDTH / 2, y, 1, sub, outline);
    }

    static void panel(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(FRAME);
        g.fillRect(x, y, w, h);
        g.setColor(new Color(90, 70, 110, 200));
        g.drawRect(x, y, w - 1, h - 1);
    }
}
