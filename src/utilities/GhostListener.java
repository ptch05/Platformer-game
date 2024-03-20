package utilities;
import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.Ghost;
import entities.Player;
import projectiles.Fireball;

public class GhostListener implements StepListener{
  
  private static final float FIRE_RANGE = 20f; // The range at which the ghost will start shooting.
  private static final float FIREBALL_SPEED = -12.0f; // Negative for left
  private float timeSinceLastShot = 0.0f;
  private Player player;
  private Ghost ghost;

  public GhostListener(Ghost ghost, Player player) {
    this.ghost = ghost;
    this.player = player;
  }
  

  @Override
    public void postStep(StepEvent e) {
      // N.A
    }

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
    }

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
