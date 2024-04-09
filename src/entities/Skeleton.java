package entities;

import city.cs.engine.*;

/**
 * Represents a skeleton enemy in the game world. Similar to the Hound, this
 * enemy patrols between defined boundaries but with different images and
 * speeds specific to the Skeleton's character.
 * 
 * The Skeleton class extends the Enemy class, making use of its patrolling
 * functionality while also defining its unique appearance and movement
 * characteristics.
 * 
 * @author Peiman Timaji, Peiman.Timaji@ac.city.uk
 * @version 1.0
 * @since 1.0
 */
public class Skeleton extends Enemy {
  private static final Shape skeletonShape = new BoxShape((float)0.8, (float)2.8);
  private static final BodyImage skeletonImageRight = new BodyImage("./assets/images/skeleton/skeleton-clothed-right.gif", 6);
  private static final BodyImage skeletonImageLeft = new BodyImage("./assets/images/skeleton/skeleton-clothed-left.gif", 6);

  public Skeleton(World world, int leftBoundary, int rightBoundary) {
    super(world, skeletonShape, skeletonImageRight, skeletonImageLeft, leftBoundary, rightBoundary, 3);
  }
  
}
