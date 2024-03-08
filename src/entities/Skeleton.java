package entities;

import city.cs.engine.BodyImage;
import city.cs.engine.BoxShape;
import city.cs.engine.Shape;
import main.GameWorld;

public class Skeleton extends Enemy {
  private static final Shape skeletonShape = new BoxShape((float)0.8, (float)2.8);
  private static final BodyImage skeletonImageRight = new BodyImage("./assets/images/skeleton/skeleton-clothed-right.gif", 6);
  private static final BodyImage skeletonImageLeft = new BodyImage("./assets/images/skeleton/skeleton-clothed-left.gif", 6);

  public Skeleton(GameWorld world, int leftBoundary, int rightBoundary) {
    super(world, skeletonShape, skeletonImageRight, skeletonImageLeft, leftBoundary, rightBoundary, 3);
  }
  
}
