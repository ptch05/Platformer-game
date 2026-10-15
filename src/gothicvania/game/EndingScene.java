package gothicvania.game;

import gothicvania.engine.Assets;
import gothicvania.engine.Audio;
import gothicvania.engine.Display;
import gothicvania.engine.Input;
import gothicvania.engine.Input.Action;
import gothicvania.engine.PixelFont;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** Victory: the castle at dawn and the totals for the whole run. */
public final class EndingScene implements Scene {
    private static final BufferedImage WIN = Assets.image("ui/win.png");
    private final Game game;
    private final RunStats run;
    private double time;

    public EndingScene(Game game, RunStats run) {
        this.game = game;
        this.run = run;
        Audio.get().music("music/menu-music-loop");
    }

    @Override
    public void update(double dt, Input input) {
        time += dt;
        if (time > 2 && input.pressed(Action.CONFIRM)) game.goTo(new TitleScene(game));
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(new Color(10, 4, 14));
        g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);
        int size = Display.HEIGHT;
        g.drawImage(WIN, 0, 0, size, size, null);
        int x = size + 14;
        Color gold = new Color(232, 196, 120);
        PixelFont.drawOutlined(g, "THE CURSE", x, 24, 2, gold, Color.BLACK);
        PixelFont.drawOutlined(g, "IS LIFTED", x, 42, 2, gold, Color.BLACK);
        String[][] rows = {
            {"TIME", RunStats.clock(run.time)},
            {"SLAIN", String.valueOf(run.kills)},
            {"SOULS", String.valueOf(run.souls)},
            {"SECRETS", run.secrets + "/" + run.totalSecrets},
            {"DEATHS", String.valueOf(run.deaths)},
            {"MAX HEALTH", String.valueOf(run.maxHp)},
        };
        int y = 74;
        for (String[] r : rows) {
            PixelFont.drawOutlined(g, r[0], x, y, 1, new Color(180, 170, 200), Color.BLACK);
            PixelFont.drawOutlined(g, r[1], Display.WIDTH - 10 - PixelFont.width(r[1], 1), y, 1, Color.WHITE, Color.BLACK);
            y += 13;
        }
        PixelFont.drawOutlined(g, "THANK YOU", x, y + 10, 1, gold, Color.BLACK);
        PixelFont.drawOutlined(g, "FOR PLAYING", x, y + 20, 1, gold, Color.BLACK);
        if (time > 2 && (int) (time * 2) % 2 == 0) PixelFont.drawOutlined(g, "PRESS ENTER", x, Display.HEIGHT - 16, 1, Color.WHITE, Color.BLACK);
    }
}
