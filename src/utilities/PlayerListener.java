package utilities;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import entities.Player;
import levels.GameLevel;
import main.GameView;

/**
 * Listens for step events in the game world to update the camera's position based
 * on the player's movement. This class ensures that the player remains in view as they
 * navigate the game world, by adjusting the game view's center to follow the player.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class PlayerListener implements StepListener {
    private World world;
    private UserView view;
    private Player player;

    public PlayerListener(World world, GameView view) { 
        this.world = world;
        this.view = view;
    }

    public void preStep(StepEvent e) {
        //N.A.
    }

    /**
     * Updates the camera's position to follow the player after each physics step.
     * The camera is centered on the player's position with a slight vertical offset
     * to provide a better view of the surrounding game world.
     *
     * @param e The event object containing information about the physics step.
     */
    public void postStep(StepEvent e) {
        Player player = ((GameLevel) world).getPlayer();
        Vec2 position = player.getPosition(); // Get the current position of the player
        Vec2 viewCentre = new Vec2(position.x, position.y + 5.3f);
        view.setCentre(viewCentre); // Now the camera follows the player but is offset 5.3 units above
    }
    
    /**
     * Sets the current player to be tracked by this listener. Allows the listener
     * to adjust if the player entity changes or needs to be updated.
     * 
     * @param newPlayer The new player entity to track.
     */
    public void setPlayer(Player newPlayer) {
        this.player = newPlayer;
    }
}