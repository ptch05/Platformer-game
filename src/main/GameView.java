package main;

import city.cs.engine.*;
import java.awt.Graphics2D;

public class GameView extends UserView {

    public GameView(World world, int width, int height) {
        super(world, width, height);
    }

    @Override
    protected void paintBackground(Graphics2D g) {
        // Implement background painting logic
    }

    @Override
    protected void paintForeground(Graphics2D g) {
        // Implement foreground painting logic, such as drawing the health bar and ammo count
    }
}
