package entities;
import city.cs.engine.*;
import utilities.GhostListener;

/**
 * The Ghost class extends the Enemy class and includes additional
 * functionality specific to ghost enemies in the game. Ghosts can be
 * killed instantly with special attacks or can take multiple hits
 * before dying. They also have the ability to shoot fireballs.
 * 
 * @author Peiman Timaji, Peiman.Timaji@ac.city.uk
 * @version 1.0
 * @since 1.0
 */
public class Ghost extends Enemy{
  private boolean isGhostAlive = true;
  private int hitsTaken;
  
  private static final Shape ghostShape = new BoxShape(1.5f, 4.5f);
  private static final BodyImage ghostImage = new BodyImage("./assets/images/ghost/ghost.gif", 7f);
  private Player player;
  private GhostListener ghostListener;
  
  public Ghost(World world, Player player) {
    super(world, ghostShape, ghostImage, ghostImage, 0, 0, 0);
    addImage(ghostImage);
    this.player = player;
    this.ghostListener = new GhostListener(this, player);
    world.addStepListener(ghostListener);
  }

  /**
   * Returns whether the ghost is currently alive.
   * 
   * @return true if the ghost is alive, false otherwise.
   */
  public boolean isGhostAlive(){
    return isGhostAlive;
  }

  /**
   * Sets the ghost's status to dead.
   */
  public void setGhostDead(){
    isGhostAlive = false;
  }

  /**
   * Processes a hit by an attack on the ghost. Special attacks
   * will kill the ghost immediately, while normal attacks will
   * require multiple hits.
   * 
   * @param isSpecialAttack true if the hit is by a special attack, false otherwise.
   */
  public void hitByAttack(boolean isSpecialAttack) {
    if (isSpecialAttack) {
      setGhostDead(); // Special attack kills immediately
    } else {
      hitsTaken++;
      if (hitsTaken >= 2) {
        setGhostDead(); // Normal attack kills after 2 hits
      }
    }
  }

  /**
   * Removes the attached step listener from the ghost, ceasing its dynamic interactions
   * with the game world and the player.
   */
  public void removeListener() {
      this.getWorld().removeStepListener(ghostListener);
  }
}
