package utilities;
import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.*;

public class DemonListener implements StepListener{
  private float timeSinceLastAttack = 0.0f;
  private Player player;
  private Demon demon;

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

    if (distance <= 15 && !demon.isDemonAttacking()  && timeSinceLastAttack >= 2f) {
      demon.demonAttack();
      timeSinceLastAttack = 0f;
    } else if (distance > 15 && demon.isDemonAttacking()) {
      demon.endDemonAttack();
      if(distance > 25){
         AudioHandler.playDemonIdleSound();
      }
    }
    demon.setLinearVelocity(new Vec2(0, 0));

  }

}

