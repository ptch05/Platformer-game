package gothicvania;

import gothicvania.engine.Display;
import gothicvania.engine.Input;
import gothicvania.game.Game;
import javax.swing.SwingUtilities;

/**
 * Entry point. Run from the project root:
 *
 * <pre>
 *   java -cp out gothicvania.Main
 *   java -cp out -Dgv.debug=true gothicvania.Main   (F1 hitboxes, F2 god mode, F3 finish level)
 * </pre>
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", System.getProperty("sun.java2d.opengl", "true"));
        SwingUtilities.invokeLater(() -> {
            Input input = new Input();
            Display display = new Display("GothicVania: Cemetery", input);
            Game game = new Game(input, display);
            display.start(game);
        });
    }
}
