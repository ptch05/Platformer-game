package utilities;

import entities.Enemy;
import entities.Hound;
import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.CollisionEvent;
import city.cs.engine.CollisionListener;
import city.cs.engine.UserView;
import entities.Player;
import entities.Skeleton;
import main.GameWorld;
import objects.Armour;
import objects.Potion;
import objects.Spikes;
import objects.Trophy;

public class PlayerCollisions implements CollisionListener {
  private Player player;
  private UserView view;
  private GameWorld world;
  public PlayerCollisions(Player p, GameWorld world){
      this.player = p;
      this.world = world;
  }

  @Override
  public void collide(CollisionEvent e) {
    if (e.getOtherBody() instanceof Skeleton || e.getOtherBody() instanceof Hound) {
      Enemy enemy = (Enemy) e.getOtherBody();
        if(player.isAttacking()){
          enemy.enemyDie();
          player.addKill();
          AudioHandler.playKillSound();
        } else {
          // Logic to knock the player back
          Vec2 knockback = new Vec2(-10, 10);
          player.setLinearVelocity(knockback);
          Vec2 playerPosition = player.getPosition();
          player.isInAir();
          Vec2 enemyPosition = e.getOtherBody().getPosition();

          // Determine the direction of the enemy relative to the player
          boolean enemyIsLeft = enemyPosition.x < playerPosition.x;
          if (enemyIsLeft) {
            player.hurtRight();
          } else {
            player.hurtLeft();
          }

          player.applyForce(new Vec2(enemyIsLeft ? 30 : -30, 10));
          player.reduceHealth(player.getDamageAmount());

          Vec2 enemyVelocity = enemy.getLinearVelocity();
          enemy.setLinearVelocity(new Vec2(0, enemyVelocity.y));
          AudioHandler.playHurtSound();
        }
    }

      if (e.getReportingBody() instanceof Player) {  //So that enemy doesn't interfere with any of these
      
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
          AudioHandler.playHurtSound();
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
          world.restartGame();
        }
      }
  }
}

