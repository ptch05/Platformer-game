package entities;

import city.cs.engine.*;

/**
 * Represents a hound enemy in the game world. This enemy has specific images
 * for moving right and left and patrolls between defined boundaries at a
 * specified speed.
 * 
 * The Hound class extends the Enemy class, utilizing its patrol mechanism
 * and integrates additional characteristics like unique shape and images
 * for directional movement.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class Hound extends Enemy {
  private static final Shape houndShape = new BoxShape((float)1.5, (float)3.05);
  private final static BodyImage houndImageRight = new BodyImage("./assets/images/hound/hound-right.gif", (float) 5.1);
  private final static BodyImage houndImageLeft = new BodyImage("./assets/images/hound/hound-left.gif", (float) 5.1);

  public Hound(World world, int leftBoundary, int rightBoundary) {
    super(world, houndShape, houndImageRight, houndImageLeft, leftBoundary, rightBoundary, 8);
  }
  
}

