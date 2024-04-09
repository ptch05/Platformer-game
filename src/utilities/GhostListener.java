package utilities;
import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.Ghost;
import entities.Player;
import projectiles.Fireball;

/**
 * The GhostListener class implements StepListener to add functionality to
 * the Ghost class. It allows the Ghost to shoot fireballs at intervals
 * towards the player's position.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class GhostListener implements StepListener{
  
  private static final float FIRE_RANGE = 25f; // The range at which the ghost will start shooting.
  private static final float FIREBALL_SPEED = -12.0f; // Negative for left
  private float timeSinceLastShot = 0.0f;
  private Player player;
  private Ghost ghost;

  public GhostListener(Ghost ghost, Player player) {
    this.ghost = ghost;
    this.player = player;
  }
  
  /**
   * Called before the simulation step is processed. It contains logic for the
   * ghost to shoot fireballs if the player is within a certain range and a set
   * amount of time has passed since the last shot.
   * 
   * @param e The event object containing information about the simulation step.
   */
  @Override
  public void preStep(StepEvent e) {
    Vec2 playerPosition = player.getPosition();
    Vec2 ghostPosition = ghost.getPosition();
    timeSinceLastShot += e.getStep();
    if(ghost.isGhostAlive()){ // So that it only shoots fireballs when the ghost is alive
      if (Math.abs(ghostPosition.x - playerPosition.x) < FIRE_RANGE && timeSinceLastShot >= 2.5f) {
          shootFireball();
          timeSinceLastShot = 0.0f; // Reset the timer after shooting
      }

      // Check each fireball's distance and destroy if beyond range
      for (Body body : ghost.getWorld().getDynamicBodies()) {
        if (body instanceof Fireball) {
            Fireball fireball = (Fireball) body;
            if (fireball.getDistanceTraveled() > FIRE_RANGE) {
                fireball.destroy();
            }
        }
      }
    }
  }

  /**
   * Called after the simulation step is processed. Currently not utilized.
   * 
   * @param e The event object containing information about the simulation step.
   */
  @Override
  public void postStep(StepEvent e) {
      // This method is not used.
  }

  private void shootFireball(){
    Vec2 position = ghost.getPosition();
    Fireball fireball = new Fireball(ghost.getWorld(), position);
    fireball.setPosition(position.add(new Vec2(-0.5f, -1))); // Start the fireball slightly to the left and a little lower
    fireball.setLinearVelocity(new Vec2(FIREBALL_SPEED, 0)); // Shoot left
    fireball.setGravityScale(0);
    AudioHandler.playFireballSound();
  }

}
