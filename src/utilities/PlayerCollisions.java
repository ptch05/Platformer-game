package utilities;

import entities.*;
import levels.*;
import main.Game;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.*;
import objects.*;
import projectiles.Fireball;

public class PlayerCollisions implements CollisionListener {
  private Player player;
  private Game g;
  
  private GameLevel gameLevel;
  public PlayerCollisions(Player p, GameLevel gameLevel, Game game){
      this.player = p;
      this.gameLevel = gameLevel;
      g = game;
  }

  @Override
  public void collide(CollisionEvent e) {
    if (e.getOtherBody() instanceof Skeleton || e.getOtherBody() instanceof Hound || e.getOtherBody() instanceof Ghost) {
      Enemy enemy = (Enemy) e.getOtherBody();
      if(player.isAttacking() || player.isSpecialAttacking()){
        boolean wasGhostAlreadyDead = false;
        if (enemy instanceof Ghost) {
            Ghost ghost = (Ghost) enemy;
            wasGhostAlreadyDead = !ghost.isGhostAlive(); // Check if the ghost was already dead before this attack
            ghost.hitByAttack(player.isSpecialAttacking()); // Handle the hit appropriately
            if (ghost.isGhostAlive() && !wasGhostAlreadyDead) {
                return; // If the ghost survived the hit, don't add kill or play sound yet
            }
        }
        enemy.enemyDie();
        player.addKill();
        AudioHandler.playKillSound();
      } else {
          // Logic to knock the player back
          player.setLinearVelocity(new Vec2(0, 0)); //Initially sets player velocity to 0 so that it kills off all the player's velocity
          Vec2 knockback = new Vec2(-15, 15);
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
  
          int healthLossAmount = (e.getOtherBody() instanceof Skeleton) ? player.getHealthLossAmount()/2 : player.getHealthLossAmount(); //Skeleton attacks do half as much damage as Hound attacks
          player.applyForce(new Vec2(enemyIsLeft ? 30 : -30, 10));
          player.reduceHealth(healthLossAmount);
  
          Vec2 enemyVelocity = enemy.getLinearVelocity();
          enemy.setLinearVelocity(new Vec2(0, enemyVelocity.y)); //So that the enemy doesn't also move after colliding
          
          
      }
  }
  
      if (e.getReportingBody() instanceof Player) {  //So that enemy doesn't interfere with any of these
        if(e.getOtherBody() instanceof Spikes){
          AudioHandler.playHurtSound();
          player.handleDeath();
        }

        else if(e.getOtherBody() instanceof Trophy){
          e.getOtherBody().destroy();
          AudioHandler.playVictorySound();
          try {
              Thread.sleep(4000,500);
                gameLevel.clearBodies();
                if(gameLevel.getLevelName().equals("Level3") && gameLevel.isComplete()){
                  player.setVictorious(true);
                } else {
                  g.goToNextLevel();
                }

          } catch (Exception exception) {
              exception.printStackTrace(); // This will print any exception that occurs
          }
      }
      
        else if(e.getOtherBody() instanceof Fireball){
          player.reduceHealth(player.getHealthLossAmount()/4*3);
          e.getOtherBody().destroy();
        }
      }
  }
}

