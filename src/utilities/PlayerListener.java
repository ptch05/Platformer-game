package utilities;

import org.jbox2d.common.Vec2;

import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import city.cs.engine.UserView;
import entities.Player;
import main.GameView;

public class PlayerListener implements StepListener {
    private Player player;
    private UserView view;

    public PlayerListener(Player player, GameView view) { 
        this.player = player;
        this.view = view;
    }
    
    public void preStep(StepEvent e) {
        //N.A.
    }
    public void postStep(StepEvent e) {
        view.setCentre(new Vec2(player.getPosition())); //Camera follows the player around constantly
    }

    public void setPlayer(Player newPlayer) {
        this.player = newPlayer;
    }
}