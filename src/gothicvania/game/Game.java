package gothicvania.game;

import gothicvania.engine.Audio;
import gothicvania.engine.Display;
import gothicvania.engine.Input;
import gothicvania.engine.Input.Action;
import gothicvania.engine.PixelFont;
import gothicvania.world.LevelData;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Owns the current {@link Scene}, fades between scenes, and handles keys that
 * work everywhere (mute, fullscreen).
 */
public final class Game implements Display.Loop {
    private static final double FADE = 0.35;

    public final Input input;
    public final List<LevelData> levels;
    public final SaveData save = new SaveData();
    public final boolean debug = Boolean.getBoolean("gv.debug");
    private final Display display;
    private Scene scene;
    private Scene next;
    private double fade; // >0 fading out to next, <0 fading in
    private double muteNotice;

    public Game(Input input, Display display) {
        this.input = input;
        this.display = display;
        this.levels = LevelData.loadAll();
        Audio.get().preload("sounds/player/jump", "sounds/player/attack", "sounds/player/hurt", "sounds/player/kill",
            "sounds/misc/fireball", "sounds/misc/fire-trap", "sounds/collectibles/potion", "sounds/player/crouch",
            "sounds/player/special-attack", "sounds/player/gain-special", "sounds/collectibles/armour",
            "sounds/demon/demon-attack", "sounds/demon/demon-idle", "music/menu-music-loop");
        scene = new TitleScene(this);
    }

    /** Fades out the current scene and switches to another. */
    public void goTo(Scene target) {
        if (next != null) return;
        next = target;
        fade = FADE;
    }

    /** Switches immediately without a fade (used by tools). */
    public void setScene(Scene target) {
        scene = target;
        next = null;
        fade = 0;
    }

    public void quit() {
        if (display != null) display.stop();
    }

    @Override
    public void update(double dt) {
        if (input.pressed(Action.MUTE)) {
            Audio.get().toggleMute();
            muteNotice = 1.5;
        }
        if (input.pressed(Action.FULLSCREEN) && display != null) display.toggleFullscreen();
        muteNotice -= dt;

        if (next != null) {
            fade -= dt;
            if (fade <= 0) {
                scene = next;
                next = null;
                fade = -FADE;
                input.clear();
            }
            return;
        }
        if (fade < 0) fade = Math.min(0, fade + dt);
        scene.update(dt, input);
    }

    @Override
    public void render(Graphics2D g) {
        scene.render(g);
        double alpha = next != null ? 1 - fade / FADE : fade < 0 ? -fade / FADE : 0;
        if (alpha > 0) {
            g.setColor(new Color(0, 0, 0, (int) (255 * Math.min(1, alpha))));
            g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);
        }
        if (muteNotice > 0) {
            String text = Audio.get().isMuted() ? "SOUND OFF" : "SOUND ON";
            PixelFont.drawOutlined(g, text, Display.WIDTH - PixelFont.width(text, 1) - 6, Display.HEIGHT - 12, 1, Color.WHITE, Color.BLACK);
        }
    }
}
