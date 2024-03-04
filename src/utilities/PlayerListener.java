package utilities;

import org.jbox2d.common.Vec2;

import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import city.cs.engine.UserView;
import entities.Player;
import main.GameView;
import main.GameWorld;

public class PlayerListener implements StepListener {
    private GameWorld world;
    private UserView view;
    private Player player;

    public PlayerListener(GameWorld world, GameView view) { 
        this.world = world;
        this.view = view;
    }

    private Player getPlayer() {
        return (Player) world.getPlayer();
    }
    
    public void preStep(StepEvent e) {
        //N.A.
    }
    public void postStep(StepEvent e) {
        Player player = getPlayer();
    // Get the current position of the player
    Vec2 position = player.getPosition();
    // Adjust the y-value by subtracting 10 units
    Vec2 viewCentre = new Vec2(position.x, position.y + (float)3.2);
    view.setCentre(viewCentre); // Now the camera follows the player but is offset 10 units above
      }
    

    public void setPlayer(Player newPlayer) {
        this.player = newPlayer;
    }
}