package gothicvania.tools;

import gothicvania.engine.Input;
import gothicvania.game.Game;
import gothicvania.game.PlayScene;
import gothicvania.game.RunStats;
import gothicvania.world.Level;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Renders a whole level (tiles and everything placed in it) to one PNG, handy
 * while editing the level files.
 *
 * <pre>java -cp out gothicvania.tools.MapRender LEVEL_INDEX OUT.png</pre>
 */
public final class MapRender {
    private MapRender() {
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("gv.silent", "true");
        Game game = new Game(new Input(), null);
        Level level = new PlayScene(game, Integer.parseInt(args[0]), new RunStats()).level();
        BufferedImage image = new BufferedImage(level.map.widthPx(), level.map.heightPx(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(40, 34, 56));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        level.renderOverview(g);
        g.dispose();
        ImageIO.write(image, "png", new File(args[1]));
        System.exit(0);
    }
}
