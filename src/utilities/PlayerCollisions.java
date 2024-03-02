package utilities;

import org.jbox2d.common.Vec2;

import city.cs.engine.CollisionEvent;
import city.cs.engine.CollisionListener;
import entities.Player;
import entities.Skeleton;
import interactables.Armour;
import interactables.Potion;
import interactables.Spikes;

public class PlayerCollisions implements CollisionListener {
  private Player player;
  public PlayerCollisions(Player p){
      this.player = p;
  }

  @Override
  public void collide(CollisionEvent e) {
      if (e.getOtherBody() instanceof Skeleton) {
        Skeleton skeleton = (Skeleton) e.getOtherBody();

          // Logic to knock the player back
          Vec2 knockback = new Vec2(-10, 10);
          player.setLinearVelocity(knockback);
          Vec2 playerPosition = player.getPosition();
          Vec2 skeletonPosition = e.getOtherBody().getPosition();

          // Determine the direction of the skeleton relative to the player
          boolean skeletonIsLeft = skeletonPosition.x < playerPosition.x;
          if (skeletonIsLeft) {
            player.hurtRight(); 
          } else {
            player.hurtLeft(); 
          }
          
          player.applyForce(new Vec2(skeletonIsLeft ? 1000 : -1000, 0));
          player.reduceHealth(player.getDamageAmount());

          Vec2 skeletonVelocity = skeleton.getLinearVelocity();
          skeleton.setLinearVelocity(new Vec2(0, skeletonVelocity.y));
          AudioHandler.playHurtSound();
      
        } 
      if (e.getReportingBody() instanceof Player) {  //So that skeleton doesn't interfere with any of these
        if (e.getOtherBody() instanceof Potion){
          player.gainHealth(20);
          e.getOtherBody().destroy();
        }
        if (e.getOtherBody() instanceof Armour){
          player.gainArmour();
          e.getOtherBody().destroy();
        }
        if(e.getOtherBody() instanceof Spikes){
          System.out.println("Sayonara");
          player.handleDeath();
        }
      }
  }
}

