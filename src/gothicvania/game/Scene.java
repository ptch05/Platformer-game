package gothicvania.game;

import gothicvania.engine.Input;
import java.awt.Graphics2D;

/** One screen of the game (title, gameplay, ending). */
public interface Scene {
    void update(double dt, Input input);

    void render(Graphics2D g);
}
