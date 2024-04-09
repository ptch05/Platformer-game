package utilities;

import entities.*;
import levels.*;
import main.Game;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.*;
import collectibles.Trophy;
import objects.*;
import projectiles.Fireball;

/**
 * Handles collision events for the player character in the game. This class is responsible
 * for determining the outcomes of collisions between the player and other game objects,
 * such as enemies, environmental hazards, and collectibles. Actions may include applying
 * damage, performing knockback effects, playing sound effects, and updating game state.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class PlayerCollisions implements CollisionListener {
  private Player player;
  private Game g;
  
  private GameLevel gameLevel;
  public PlayerCollisions(Player p, GameLevel gameLevel, Game game){  
      this.player = p;
      this.gameLevel = gameLevel;
      g = game;
  }

  /**
   * Responds to collision events involving the player. This method determines the type
   * of the other body involved in the collision and executes appropriate actions such
   * as applying damage to the player, destroying enemies, or triggering game events.
   *
   * @param e The collision event.
   */
  @Override
  public void collide(CollisionEvent e) {
    if (e.getOtherBody() instanceof Skeleton || e.getOtherBody() instanceof Hound || e.getOtherBody() instanceof Ghost || e.getOtherBody() instanceof Demon) {
      Enemy enemy = (Enemy) e.getOtherBody();
      if(player.isAttacking() || player.isSpecialAttacking()){
        if (enemy instanceof Ghost) {
            boolean wasGhostAlreadyDead = false;
            Ghost ghost = (Ghost) enemy;
            wasGhostAlreadyDead = !ghost.isGhostAlive(); // Check if the ghost was already dead before this attack
            ghost.hitByAttack(player.isSpecialAttacking()); // Handle the hit appropriately
            if (ghost.isGhostAlive() && !wasGhostAlreadyDead) {
                return; // If the ghost survived the hit, don't add kill or play sound yet
            }
        }
        if(enemy instanceof Demon){
          boolean wasDemonAlreadyDead = false;
          Demon demon = (Demon) enemy;
          wasDemonAlreadyDead = !demon.isDemonAlive();
          if(demon.isDemonAttacking())
          demon.hitByAttack(player.isSpecialAttacking());
          if (demon.isDemonAlive() && !wasDemonAlreadyDead) {
            return; 
          }
        }
        enemy.enemyDie();
        player.addKill();
        AudioHandler.playKillSound();
      } else {
         performKnockback(e, enemy);
         AudioHandler.playHurtSound();
      }
    }

    if (e.getReportingBody() instanceof Player) {  //So that enemy doesn't interfere with any of these
      if(e.getOtherBody() instanceof Spikes || e.getOtherBody() instanceof Lava){
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
              player.setVictorious(true); //Player wins if they collide with the trophy and have the allocated number of kills to complete the level
            } else {
              g.goToNextLevel();
            }
        } catch (Exception exception) {
            exception.printStackTrace(); // This will print any exception that occurs
        }
      }
    
      else if(e.getOtherBody() instanceof Fireball){
        if(!player.isCrouching()){
          performKnockback(e, null);
          player.reduceHealth(player.getHealthLossAmount()/4*3);
          AudioHandler.playHurtSound();
          e.getOtherBody().destroy();
        }
      }

      else if(e.getOtherBody() instanceof FireTrap){
        performKnockback(e, null);
        player.reduceHealth(player.getHealthLossAmount()/2);
        AudioHandler.playHurtSound();
      }
    }
  }

  /**
   * Applies a knockback effect to the player character upon collision with enemies or certain hazards.
   * This method adjusts the player's velocity and position based on the direction and type of collision.
   *
   * @param e The collision event.
   * @param enemy The enemy involved in the collision, if applicable. Can be null for non-enemy collisions.
   */
  private void performKnockback(CollisionEvent e, Enemy enemy){
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

     if(enemy !=null){ //So that it only uses the above logic for the firetrap and fireball 
      int healthLossAmount = (e.getOtherBody() instanceof Skeleton) ? player.getHealthLossAmount()/2 : player.getHealthLossAmount(); //Skeleton attacks do half as much damage as Hound attacks
      player.applyForce(new Vec2(enemyIsLeft ? 30 : -30, 10));
      player.reduceHealth(healthLossAmount);

      Vec2 enemyVelocity = enemy.getLinearVelocity();
      enemy.setLinearVelocity(new Vec2(0, enemyVelocity.y)); //So that the enemy doesn't also move after colliding
      } else{
        return;
    }
  }
}

