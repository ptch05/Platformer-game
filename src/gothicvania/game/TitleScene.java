package gothicvania.game;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Display;
import gothicvania.engine.Input;
import gothicvania.engine.Input.Action;
import gothicvania.engine.Particles;
import gothicvania.engine.PixelFont;
import gothicvania.world.Background;
import gothicvania.world.Theme;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/** The title screen: main menu, chapter select and the controls page. */
public final class TitleScene implements Scene {
    private static final Color GOLD = new Color(232, 196, 120);
    private static final Color DIM = new Color(150, 140, 170);
    private static final BufferedImage LOGO = Assets.image("ui/logo.png");

    private enum Page { MAIN, CHAPTERS, CONTROLS }

    private final Game game;
    private final Background background = new Background(Theme.CEMETERY, Display.HEIGHT);
    private final Particles particles = new Particles();
    private Page page = Page.MAIN;
    private int choice;
    private double time;

    public TitleScene(Game game) {
        this.game = game;
        Audio.get().music("music/menu-music-loop");
    }

    private List<String> mainItems() {
        List<String> items = new ArrayList<>();
        items.add("NEW GAME");
        if (game.save.unlocked() > 1) items.add("CHAPTER SELECT");
        items.add("CONTROLS");
        items.add("QUIT");
        return items;
    }

    @Override
    public void update(double dt, Input input) {
        time += dt;
        particles.update(dt);
        if (Math.random() < dt * 8) {
            particles.emit(Math.random() * Display.WIDTH, Display.HEIGHT * (0.6 + Math.random() * 0.4), 1,
                Particles.burst().speed(4, 14).direction(Math.random() < 0.5 ? 0 : 180, 20).life(3, 6).size(3, 6)
                    .colors(0x2A2440, 0x342C4E).fade());
        }
        if (Math.random() < dt * 3) {
            particles.emit(Math.random() * Display.WIDTH, Display.HEIGHT * Math.random(), 1,
                Particles.burst().speed(2, 8).life(1.5, 3).size(1, 1).colors(0x9CF6FF, 0x6FE8FF).fade());
        }

        switch (page) {
            case MAIN -> {
                List<String> items = mainItems();
                choice = navigate(input, choice, items.size());
                if (input.pressed(Action.CONFIRM)) {
                    click();
                    switch (items.get(choice)) {
                        case "NEW GAME" -> game.goTo(new PlayScene(game, 0, new RunStats()));
                        case "CHAPTER SELECT" -> {
                            page = Page.CHAPTERS;
                            choice = 0;
                        }
                        case "CONTROLS" -> page = Page.CONTROLS;
                        default -> game.quit();
                    }
                }
            }
            case CHAPTERS -> {
                int unlocked = Math.min(game.save.unlocked(), game.levels.size());
                choice = navigate(input, choice, unlocked);
                if (input.pressed(Action.CONFIRM)) {
                    click();
                    game.goTo(new PlayScene(game, choice, new RunStats()));
                } else if (input.pressed(Action.BACK)) {
                    page = Page.MAIN;
                    choice = 1;
                }
            }
            case CONTROLS -> {
                if (input.pressed(Action.BACK) || input.pressed(Action.CONFIRM)) {
                    page = Page.MAIN;
                    choice = 0;
                }
            }
        }
    }

    private static int navigate(Input input, int choice, int count) {
        if (input.pressed(Action.UP)) {
            Audio.get().play("sounds/misc/button", 0.3f, 1.3f);
            return (choice + count - 1) % count;
        }
        if (input.pressed(Action.DOWN)) {
            Audio.get().play("sounds/misc/button", 0.3f, 1.3f);
            return (choice + 1) % count;
        }
        return Math.min(choice, count - 1);
    }

    private static void click() {
        Audio.get().play("sounds/misc/button", 0.6f, 1f);
    }

    @Override
    public void render(Graphics2D g) {
        background.render(g, time * 14, 0);
        particles.render(g, 0, 0);
        g.setColor(new Color(4, 2, 10, 90));
        g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);

        int logoY = 14 + (int) Math.round(Math.sin(time * 1.2) * 2);
        g.drawImage(LOGO, (Display.WIDTH - LOGO.getWidth()) / 2, logoY, null);

        switch (page) {
            case MAIN -> renderMenu(g, mainItems(), 128);
            case CHAPTERS -> renderChapters(g);
            case CONTROLS -> renderControls(g);
        }
        PixelFont.drawOutlined(g, "ART: ANSIMUZ (GOTHICVANIA)", 6, Display.HEIGHT - 10, 1, new Color(90, 80, 110), Color.BLACK);
    }

    private void renderMenu(Graphics2D g, List<String> items, int y) {
        for (int i = 0; i < items.size(); i++) {
            boolean sel = i == choice;
            Color c = sel ? ((int) (time * 3) % 2 == 0 ? Color.WHITE : GOLD) : DIM;
            String text = sel ? "> " + items.get(i) + " <" : items.get(i);
            PixelFont.drawCentered(g, text, Display.WIDTH / 2, y + i * 15, 1, c, Color.BLACK);
        }
    }

    private void renderChapters(Graphics2D g) {
        int unlocked = Math.min(game.save.unlocked(), game.levels.size());
        PixelFont.drawCentered(g, "CHAPTER SELECT", Display.WIDTH / 2, 112, 1, GOLD, Color.BLACK);
        for (int i = 0; i < game.levels.size(); i++) {
            boolean open = i < unlocked, sel = i == choice;
            String name = open ? PlayScene.roman(i + 1) + ". " + game.levels.get(i).name : PlayScene.roman(i + 1) + ". ???";
            String rank = open ? "RANK " + game.save.bestRank(i) : "";
            Color c = !open ? new Color(70, 60, 80) : sel ? Color.WHITE : DIM;
            int y = 128 + i * 13;
            PixelFont.drawOutlined(g, (sel ? "> " : "  ") + name, 90, y, 1, c, Color.BLACK);
            PixelFont.drawOutlined(g, rank, 310 - PixelFont.width(rank, 1), y, 1, c, Color.BLACK);
        }
        PixelFont.drawCentered(g, "ESC: BACK", Display.WIDTH / 2, 200, 1, new Color(110, 100, 130), Color.BLACK);
    }

    private void renderControls(Graphics2D g) {
        String[][] rows = {
            {"MOVE", "A D  /  ARROWS"},
            {"JUMP", "SPACE  /  W"},
            {"ATTACK", "K  /  J"},
            {"DASH", "SHIFT"},
            {"BLOODTHIRST", "G  (WHEN FULL)"},
            {"CROUCH / DROP", "S  (+ JUMP ON A LEDGE)"},
            {"PAUSE", "ESC"},
        };
        Hud.panel(g, 70, 104, 260, 112);
        int y = 112;
        for (String[] r : rows) {
            PixelFont.drawOutlined(g, r[0], 82, y, 1, GOLD, Color.BLACK);
            PixelFont.drawOutlined(g, r[1], 318 - PixelFont.width(r[1], 1), y, 1, Color.WHITE, Color.BLACK);
            y += 11;
        }
        PixelFont.drawCentered(g, "WALL-JUMP: JUMP WHILE SLIDING", Display.WIDTH / 2, y + 3, 1, DIM, Color.BLACK);
        PixelFont.drawCentered(g, "SWING AT FIREBALLS TO RETURN THEM", Display.WIDTH / 2, y + 13, 1, DIM, Color.BLACK);
    }
}
