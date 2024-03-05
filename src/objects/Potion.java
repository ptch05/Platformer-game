package objects;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.Player;

public class Potion extends StaticBody{
  private static final Shape potionShape = new BoxShape((float)0.1, (float)0.5);
  private static final BodyImage potionImage = new BodyImage("./assets/images/misc/potion.png", (float) 2.5);

  private Sensor potionSensor;

    public Potion(World world) {
        super(world);
        potionSensor = new Sensor(this, potionShape); // Create the sensor
        potionSensor.addSensorListener(new PotionSensorListener()); 
        addImage(potionImage);
    }
    
    // Inner class or separate class to handle potion pickup
    private class PotionSensorListener implements SensorListener {
        @Override
        public void beginContact(SensorEvent e) {
            if (e.getContactBody() instanceof Player) {
                Player player = (Player) e.getContactBody();
                player.gainHealth(20);
                AudioHandler.playPotionSound();
                Potion.this.destroy(); 
            }
        }

        @Override
        public void endContact(SensorEvent arg0) {
        }
}
}