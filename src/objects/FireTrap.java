package objects;
import java.util.Timer;
import java.util.TimerTask;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.Player;

public class FireTrap extends StaticBody {

  private static final Shape fireTrapShape = new BoxShape(0.05f,0.1f);
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
  

  
  private void startFiringSound(){
    float distance = Math.abs(this.getPosition().x-player.getPosition().x);
    if(distance <=22.5 ){
      AudioHandler.playFireTrapSound();
    } else{
      AudioHandler.stopFireTrapSound();
    }
  }

}
