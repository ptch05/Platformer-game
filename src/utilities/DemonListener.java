package utilities;
import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.*;

/**
 * A step listener for the Demon entity that handles the timing of its attacks
 * and sounds. It tracks the time since the last attack and the distance to the
 * player to determine when to attack next.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */

public class DemonListener implements StepListener{
  private float timeSinceLastAttack = 0.0f;
  private Player player;
  private Demon demon;

  /**
   * Initializes a DemonListener with the given demon and player.
   * 
   * @param demon The demon entity to listen for.
   * @param player The player entity to interact with.
   */
  public DemonListener(Demon demon, Player player) {
    this.demon = demon;
    this.player = player;
  }
  

  @Override
    public void postStep(StepEvent e) {
      // N.A
    }

  @Override
  public void preStep(StepEvent e) {
    Vec2 playerPosition = player.getPosition();
    Vec2 demonPosition = demon.getPosition();
    float distance = Math.abs(playerPosition.x - demonPosition.x);

    timeSinceLastAttack += e.getStep();
    if(demon.isDemonAlive()){
      if (distance <= 20 && !demon.isDemonAttacking()  && timeSinceLastAttack >= 2f) {
        demon.demonAttack();
        timeSinceLastAttack = 0f;
      } else if (demon.isDemonAttacking() &&  timeSinceLastAttack >= 1f) {
        // End the attack if currently attacking and it's been 1 seconds
        demon.endDemonAttack();
      }

      if(distance > 25){
        AudioHandler.playDemonIdleSound();
      }
      demon.setLinearVelocity(new Vec2(0, 0));
    }
  }
}

