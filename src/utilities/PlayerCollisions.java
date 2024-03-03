package utilities;

import org.jbox2d.common.Vec2;

import city.cs.engine.CollisionEvent;
import city.cs.engine.CollisionListener;
import city.cs.engine.UserView;
import entities.Player;
import entities.Skeleton;
import interactables.Armour;
import interactables.Potion;
import interactables.Spikes;
import interactables.Trophy;

public class PlayerCollisions implements CollisionListener {
  private Player player;
  private UserView view;
  public PlayerCollisions(Player p){
      this.player = p;
  }

  @Override
  public void collide(CollisionEvent e) {
      if (e.getOtherBody() instanceof Skeleton) {
        Skeleton skeleton = (Skeleton) e.getOtherBody();
        if(player.isAttacking()){
          skeleton.destroy();
          AudioHandler.playKillSound();
        } else {
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
      }

      if (e.getReportingBody() instanceof Player) {  //So that skeleton doesn't interfere with any of these
      
        if (e.getOtherBody() instanceof Potion){
          player.gainHealth(20);
          e.getOtherBody().destroy();
        }

        if (e.getOtherBody() instanceof Armour){
          player.gainArmour();
          e.getOtherBody().destroy();
          view.repaint();
        }

        if(e.getOtherBody() instanceof Spikes){
          System.out.println("Sayonara");
          player.handleDeath();
        }

        if(e.getOtherBody() instanceof Trophy){
          System.out.println("You win!");
          e.getOtherBody().destroy();
          AudioHandler.playVictorySound();
          try {
            Thread.sleep(4000,500);
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
          player.destroy();
        }
      }
  }
}

