package gothicvania.game;

import gothicvania.engine.Audio;
import gothicvania.engine.Display;
import gothicvania.engine.Input;
import gothicvania.engine.Input.Action;
import gothicvania.engine.PixelFont;
import gothicvania.world.Level;
import gothicvania.world.LevelData;
import gothicvania.world.LevelProgress;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Plays one level: runs the {@link Level}, shows the HUD, the level title card,
 * the pause menu and the results card. Dying rebuilds the level from the last
 * checkpoint while keeping what was collected.
 */
public final class PlayScene implements Scene, Level.Listener {
    private static final String[] PAUSE_ITEMS = {"RESUME", "RETRY FROM CHECKPOINT", "QUIT TO TITLE"};
    private static final Color GOLD = new Color(232, 196, 120);

    private final Game game;
    private final int index;
    private final RunStats run;
    private final LevelData data;
    private final LevelProgress progress = new LevelProgress();
    private final Hud hud = new Hud();
    private Level level;
    private double introTime;
    private double respawnFade;
    private boolean paused;
    private int pauseChoice;
    private boolean complete;
    private double completeTime;
    private boolean godMode;
    private boolean hitboxes;

    public PlayScene(Game game, int index, RunStats run) {
        this.game = game;
        this.index = index;
        this.run = run;
        this.data = game.levels.get(index);
        build();
        if (data.music.equalsIgnoreCase("none")) Audio.get().stopMusic(1f);
        else Audio.get().music(data.music);
    }

    private void build() {
        level = new Level(data, progress, run.maxHp + progress.hearts * 20, this);
        level.player().god = godMode;
        level.setDebugHitboxes(hitboxes);
    }

    /** Exposed for the screenshot tool. */
    public Level level() {
        return level;
    }

    // ------------------------------------------------------------------ Level.Listener

    @Override
    public void onBanner(String title, String subtitle) {
        hud.banner(title, subtitle);
    }

    @Override
    public void onPlayerDied() {
        progress.deaths++;
        build();
        respawnFade = 0.6;
        hud.banner(null, "DEATH " + progress.deaths + " - RISE AGAIN");
        if (!data.music.equalsIgnoreCase("none")) Audio.get().music(data.music);
        else Audio.get().stopMusic(0.5f);
    }

    @Override
    public void onLevelComplete() {
        complete = true;
        completeTime = 0;
        run.maxHp = level.player().maxHp;
        run.add(progress);
        game.save.recordClear(index, progress.rank(), game.levels.size());
    }

    // ------------------------------------------------------------------ update

    @Override
    public void update(double dt, Input input) {
        introTime += dt;
        respawnFade = Math.max(0, respawnFade - dt);
        if (complete) {
            completeTime += dt;
            if (completeTime > 1 && input.pressed(Action.CONFIRM)) advance();
            return;
        }
        if (paused) {
            updatePause(input);
            return;
        }
        if (input.pressed(Action.PAUSE)) {
            paused = true;
            pauseChoice = 0;
            Audio.get().duckMusic(true);
            Audio.get().play("sounds/misc/button", 0.5f, 1f);
            return;
        }
        if (game.debug) debugKeys(input);
        level.update(dt, input);
        hud.update(dt, level);
    }

    private void updatePause(Input input) {
        if (input.pressed(Action.UP)) pauseChoice = (pauseChoice + PAUSE_ITEMS.length - 1) % PAUSE_ITEMS.length;
        if (input.pressed(Action.DOWN)) pauseChoice = (pauseChoice + 1) % PAUSE_ITEMS.length;
        if (input.pressed(Action.UP) || input.pressed(Action.DOWN)) Audio.get().play("sounds/misc/button", 0.3f, 1.3f);
        if (input.pressed(Action.PAUSE) && !input.pressed(Action.CONFIRM)) {
            resume();
            return;
        }
        if (!input.pressed(Action.CONFIRM)) return;
        Audio.get().play("sounds/misc/button", 0.5f, 1f);
        switch (pauseChoice) {
            case 0 -> resume();
            case 1 -> {
                resume();
                progress.deaths++;
                build();
                respawnFade = 0.6;
            }
            default -> {
                Audio.get().duckMusic(false);
                game.goTo(new TitleScene(game));
            }
        }
    }

    private void resume() {
        paused = false;
        Audio.get().duckMusic(false);
    }

    private void advance() {
        if (index + 1 < game.levels.size()) game.goTo(new PlayScene(game, index + 1, run));
        else game.goTo(new EndingScene(game, run));
    }

    private void debugKeys(Input input) {
        if (input.pressed(Action.DEBUG_HITBOXES)) {
            hitboxes = !hitboxes;
            level.setDebugHitboxes(hitboxes);
        }
        if (input.pressed(Action.DEBUG_GOD)) {
            godMode = !godMode;
            level.player().god = godMode;
            hud.banner(null, godMode ? "GOD MODE ON" : "GOD MODE OFF");
        }
        if (input.pressed(Action.DEBUG_SKIP)) level.onTrophyClaimed();
    }

    // ------------------------------------------------------------------ render

    @Override
    public void render(Graphics2D g) {
        level.render(g);
        if (!complete) hud.render(g, level);
        if (introTime < 3.2 && progress.deaths == 0) renderIntro(g);
        if (respawnFade > 0) {
            g.setColor(new Color(0, 0, 0, (int) (255 * Math.min(1, respawnFade / 0.6))));
            g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);
        }
        if (paused) renderPause(g);
        if (complete) renderResults(g);
    }

    private void renderIntro(Graphics2D g) {
        double t = introTime;
        float a = (float) Math.max(0, Math.min(1, Math.min((t - 0.2) / 0.5, (3.2 - t) / 0.6)));
        if (a <= 0) return;
        int alpha = (int) (255 * a);
        String chapter = "CHAPTER " + roman(index + 1);
        PixelFont.drawCentered(g, chapter, Display.WIDTH / 2, 70, 1, new Color(200, 190, 210, alpha), new Color(0, 0, 0, alpha));
        PixelFont.drawCentered(g, data.name, Display.WIDTH / 2, 84, 3, new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), alpha), new Color(0, 0, 0, alpha));
        if (!data.subtitle.isEmpty()) {
            PixelFont.drawCentered(g, data.subtitle, Display.WIDTH / 2, 114, 1, new Color(170, 160, 190, alpha), new Color(0, 0, 0, alpha));
        }
    }

    private void renderPause(Graphics2D g) {
        g.setColor(new Color(6, 2, 12, 190));
        g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);
        PixelFont.drawCentered(g, "PAUSED", Display.WIDTH / 2, 60, 3, GOLD, Color.BLACK);
        for (int i = 0; i < PAUSE_ITEMS.length; i++) {
            boolean sel = i == pauseChoice;
            String text = sel ? "> " + PAUSE_ITEMS[i] + " <" : PAUSE_ITEMS[i];
            PixelFont.drawCentered(g, text, Display.WIDTH / 2, 100 + i * 14, 1, sel ? Color.WHITE : new Color(150, 140, 170), Color.BLACK);
        }
        PixelFont.drawCentered(g, "M: SOUND   F: FULLSCREEN", Display.WIDTH / 2, Display.HEIGHT - 20, 1, new Color(120, 110, 140), Color.BLACK);
    }

    private void renderResults(Graphics2D g) {
        float a = (float) Math.min(1, completeTime / 0.6);
        g.setColor(new Color(6, 2, 12, (int) (215 * a)));
        g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);
        if (a < 1) return;
        int cx = Display.WIDTH / 2;
        PixelFont.drawCentered(g, data.name + " CLEARED", cx, 26, 2, GOLD, Color.BLACK);
        String[][] rows = {
            {"TIME", RunStats.clock(progress.time)},
            {"ENEMIES SLAIN", String.valueOf(progress.kills)},
            {"SOULS GATHERED", String.valueOf(progress.souls)},
            {"SECRETS", progress.secretsFound.size() + " / " + progress.totalSecrets},
            {"DEATHS", String.valueOf(progress.deaths)},
        };
        int y = 60;
        for (String[] row : rows) {
            PixelFont.drawOutlined(g, row[0], cx - 110, y, 1, new Color(180, 170, 200), Color.BLACK);
            PixelFont.drawOutlined(g, row[1], cx + 110 - PixelFont.width(row[1], 1), y, 1, Color.WHITE, Color.BLACK);
            y += 14;
        }
        String rank = progress.rank();
        Color rankColor = switch (rank) {
            case "S" -> new Color(255, 226, 122);
            case "A" -> new Color(110, 232, 255);
            case "B" -> new Color(180, 255, 160);
            default -> new Color(200, 190, 210);
        };
        PixelFont.drawCentered(g, "RANK", cx, y + 8, 1, new Color(180, 170, 200), Color.BLACK);
        PixelFont.drawCentered(g, rank, cx, y + 20, 4, rankColor, Color.BLACK);
        if ((int) (completeTime * 2) % 2 == 0) {
            PixelFont.drawCentered(g, "PRESS ENTER TO CONTINUE", cx, Display.HEIGHT - 18, 1, Color.WHITE, Color.BLACK);
        }
    }

    static String roman(int n) {
        return switch (n) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            default -> String.valueOf(n);
        };
    }
}
