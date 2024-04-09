package collectibles;

import java.util.Timer;
import java.util.TimerTask;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.Player;
/**
 * Represents an armour collectible in the game. The armour gives a temporary
 * damage reduction for the player. It uses a sensor to detect when the
 * player comes into contact with it and applies the armour effect.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */

public class Armour extends StaticBody{
  /**
   * The shape of the armour collectible.
   */
  private static final Shape armourShape = new BoxShape((float)0.1, (float)0.5);
  /**
   * The image for the armour collectible.
   */
  private static final BodyImage armourImage = new BodyImage("./assets/images/collectibles/armour.png", (float) 2.5);

  private Sensor armourSensor;

  /**
   * Creates an armour collectible in the specified world.
   * The collectible has a sensor attached to it to detect
   * player contact and apply the armour effect.
   * 
   * @param world the game world to which armour is added
   */

    public Armour(World world) {
        super(world);
        armourSensor = new Sensor(this, armourShape);
        armourSensor.addSensorListener(new ArmourSensorListener()); 
        addImage(armourImage);
    }
    
    /**
     * Listens for contact with the player to apply the armour effect.
     */
    private class ArmourSensorListener implements SensorListener {
       /**
        * Apply the armour effect to the player and set a timer
        * to remove the effect after a delay when contact begins.
        * 
        * @param e the event that characterizes the contact
        */
        @Override
        public void beginContact(SensorEvent e) {
            if (e.getContactBody() instanceof Player) {
                Player player = (Player) e.getContactBody();
                player.gainArmour();
                AudioHandler.playArmourSound();
                Armour.this.destroy(); // Destroys the armour collectible

                // Set a timer to remove the armour effect after 10 seconds
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                  @Override
                  public void run() {
                      player.loseArmour(); // Remove the armour effect
                      AudioHandler.loseArmourSound();
                  }
                }, 10000); 
            }
        }

        /**
         * Not used, but required by the SensorListener interface.
         * 
         * @param e the event that characterizes the end of contact
         */

        @Override
        public void endContact(SensorEvent e) {
          //N.A.
        }
    }
}