package objects;

import java.util.Timer;
import java.util.TimerTask;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.Player;

public class Armour extends StaticBody{
  private static final Shape armourShape = new BoxShape((float)0.1, (float)0.5);
  private static final BodyImage armourImage = new BodyImage("./assets/images/collectibles/armour.png", (float) 2.5);

  private Sensor armourSensor;

    public Armour(World world) {
        super(world);
        armourSensor = new Sensor(this, armourShape); // Create the sensor
        armourSensor.addSensorListener(new ArmourSensorListener()); 
        addImage(armourImage);
    }
    
    // Inner class or separate class to handle potion pickup
    private class ArmourSensorListener implements SensorListener {
        @Override
        public void beginContact(SensorEvent e) {
            if (e.getContactBody() instanceof Player) {
                Player player = (Player) e.getContactBody();
                player.gainArmour();
                AudioHandler.playArmourSound();
                Armour.this.destroy(); // Destroys the potion

                // Set a timer to remove the armour effect after 10 seconds
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                  @Override
                  public void run() {
                      player.loseArmour(); // Call the method to remove the armour effect
                      AudioHandler.loseArmourSound();
                  }
                }, 10000); 
            }
        }

        @Override
        public void endContact(SensorEvent arg0) {
          //N.A.
        }
}
}