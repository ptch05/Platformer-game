package objects;
import java.util.Timer;
import java.util.TimerTask;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.Player;

/**
 * Represents a fire trap within the game world. This static body
 * periodically activates, playing a sound effect to indicate its firing
 * state. The sound effect's playback is dependent on the proximity of the
 * player character to the trap.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class FireTrap extends StaticBody {

  private static final Shape fireTrapShape = new BoxShape(0.04f,0.08f);
  private static BodyImage fireTrapImage = new BodyImage("./assets/images/level-data/level3/fire-trap.gif", 9f);
  private Player player;
  private Timer timer; 
  
  public FireTrap(World world, Player player) {
    super(world, fireTrapShape);
    this.player = player;
    addImage(fireTrapImage);
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        startFiringSound();
      }
    }, 0, 1000); //Constantly plays the sound if the player is near enough to it
  }
  
  /**
   * Initiates playback of the fire trap's sound effect if the player is within a certain
   * distance. Stops the sound effect if the player moves away beyond this distance.
   * The method is called periodically by a TimerTask.
   */
  private void startFiringSound(){
    float distance = Math.abs(this.getPosition().x-player.getPosition().x);
    if(distance <=30 ){
      AudioHandler.playFireTrapSound();
    } else{
      AudioHandler.stopFireTrapSound();
    }
  }

}
