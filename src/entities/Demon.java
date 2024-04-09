package entities;
import audio.AudioHandler;
import city.cs.engine.*;
import utilities.DemonListener;

/**
 * Represents a Demon enemy in the game with capabilities to attack the player.
 * The demon has different states for idling and attacking and responds to player's
 * actions. It maintains a count of hits taken and can switch between being alive
 * and defeated.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */


public class Demon extends Enemy{
  private int hitsTaken;
  private boolean isDemonAttacking = false;
  private boolean isDemonAlive = true;
  
  private static final Shape demonShape = new BoxShape(2.8f, 3f);
  private static final Shape demonAttackShape = new BoxShape(5.75f,3f);
  private static BodyImage demonIdleImage = new BodyImage("./assets/images/demon/demon-idle.gif", 12.5f);
  private static BodyImage demonAttackImage = new BodyImage("./assets/images/demon/demon-attack.gif", 14f);
  private Player player;
  private Fixture demonAttackFixture;

  /**
   * Creates a Demon in the game world and sets its initial state and image.
   * 
   * @param world The game world where the demon will exist.
   * @param player The player character to interact with.
   */
  
  public Demon(World world, Player player) {
    super(world, demonShape, demonIdleImage, demonIdleImage, 0, 0, 0);
    addImage(demonIdleImage);
    this.player = player;
    DemonListener listener = new DemonListener(this, player);
    world.addStepListener(listener);
    this.setGravityScale(0);
  }

  /**
   * Checks if the demon is currently in the attacking state.
   * 
   * @return true if the demon is attacking, false otherwise.
   */
  public boolean isDemonAttacking(){
    return isDemonAttacking;
  }

  /**
   * Initiates the demon's attack, changing its image to the attack image,
   * playing the attack sound, and updating the attacking state.
   */
  public void demonAttack(){
    isDemonAttacking = true;
    removeAllImages();
    addImage(demonAttackImage);
    if (demonAttackFixture != null) {
      demonAttackFixture.destroy();
    }
    demonAttackFixture = new SolidFixture(this, demonAttackShape); 
    AudioHandler.playDemonAttackSound();
  }

  /**
   * Ends the demon's attack by reverting its image to the idle image and
   * resetting the attacking state.
   */

  public void endDemonAttack(){
    isDemonAttacking = false;
    removeAllImages();
    addImage(demonIdleImage);
    if (demonAttackFixture != null) {
      demonAttackFixture.destroy(); // Remove the attack hitbox using destroy()
      demonAttackFixture = new SolidFixture(this, demonShape);
    }
  }

  public int getHitsTaken(){
    return hitsTaken;
  }

  public boolean isDemonAlive(){
    return isDemonAlive;
  }


  /**
   * Processes a hit on the demon by the player. If it's a special attack,
   * it is handled differently than a normal attack.
   * 
   * @param isSpecialAttack true if the player performed a special attack, false for a normal attack.
   */
  public void hitByAttack(boolean isSpecialAttack) {
    if (isSpecialAttack) {
      hitsTaken++; 
    } else {
      hitsTaken++;
      if (hitsTaken >= 6) {
        isDemonAlive = false;
        AudioHandler.stopDemonAttackSound();
        AudioHandler.stopDemonIdleSound();
      }
    }
  }
}
