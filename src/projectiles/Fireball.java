package projectiles;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;

/**
 * Represents a fireball projectile in the game world. It is created and shot by
 * the Ghost class and has properties defining its appearance and behavior.
 * The fireball keeps track of the distance traveled since it was shot.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class Fireball extends DynamicBody {

  private static final Shape fireballShape = new BoxShape(0.25f, 0.5f);
  private static final BodyImage fireballImage = new BodyImage("./assets/images/ghost/fireball.gif", 5.5f);
  private Vec2 startPosition;

  /**
   * Creates a new fireball in the specified world at the given position.
   * 
   * @param world    The game world where the fireball is to exist.
   * @param position The initial position of the fireball.
   */

  public Fireball(World world, Vec2 position) {
    super(world, fireballShape);
    addImage(fireballImage);
    this.startPosition = position.clone();
  }

  /**
   * Calculates and returns the total distance the fireball has traveled since its creation.
   * 
   * @return The distance traveled by the fireball.
   */
  public float getDistanceTraveled() {
    return getPosition().sub(startPosition).length();
  }
  
}
