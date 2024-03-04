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
        view.setCentre(new Vec2(player.getPosition())); //Camera follows the player around constantly
    }

    public void setPlayer(Player newPlayer) {
        this.player = newPlayer;
    }
}