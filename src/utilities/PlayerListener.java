package utilities;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import entities.Player;
import levels.GameLevel;
import main.GameView;

public class PlayerListener implements StepListener {
    private World world;
    private UserView view;
    private Player player;
    private GameLevel gameLevel;

    public PlayerListener(World world, GameView view) { 
        this.world = world;
        this.view = view;
    }

    public void preStep(StepEvent e) {
        //N.A.
    }

    public void postStep(StepEvent e) {
        Player player = gameLevel.getPlayer();
        Vec2 position = player.getPosition(); // Get the current position of the player
        Vec2 viewCentre = new Vec2(position.x, position.y + (float)3.2);
        view.setCentre(viewCentre); // Now the camera follows the player but is offset 3.2units above
    }
    

    public void setPlayer(Player newPlayer) {
        this.player = newPlayer;
    }
}