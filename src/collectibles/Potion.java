package collectibles;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.Player;
/**
 * Defines a potion collectible that can be picked up by the player.
 * When the player contacts the potion, they gain health. The potion
 * uses a sensor to detect collisions and is represented as a static
 * body in the world.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */

public class Potion extends StaticBody{
  /**
   * The shape of the potion collectible.
   */
  private static final Shape potionShape = new BoxShape((float)0.1, (float)0.5);
  /**
   * The image for the potion collectible.
   */
  private static final BodyImage potionImage = new BodyImage("./assets/images/collectibles/potion.png", (float) 2.5);

  private Sensor potionSensor;

  public Potion(World world) {
    super(world);
    potionSensor = new Sensor(this, potionShape); // Create the sensor
    potionSensor.addSensorListener(new PotionSensorListener()); 
    addImage(potionImage);
   }
    
   /**
   * Inner class to handle the player's pickup of the potion.
   */
   private class PotionSensorListener implements SensorListener {
        /**
         * Handles the event where the player begins contact with the potion.
         * The player gains health, and the potion is destroyed.
         * 
         * @param e the sensor event that triggered the contact
         */
        @Override
        public void beginContact(SensorEvent e) {
            if (e.getContactBody() instanceof Player) {
                Player player = (Player) e.getContactBody();
                player.gainHealth(20);
                AudioHandler.playPotionSound();
                Potion.this.destroy(); 
            }
        }

        /**
         * Handles the event where the player ends contact with the potion.
         * Not used in this context.
         * 
         * @param e the sensor event that triggered the end of contact
         */
        @Override
        public void endContact(SensorEvent e) {
            // This method is not used because the potion is destroyed on contact.
        }
    }
}