package entities;

import city.cs.engine.BodyImage;
import city.cs.engine.BoxShape;
import city.cs.engine.Shape;
import main.GameWorld;

public class Hound extends Enemy {
  private static final Shape houndShape = new BoxShape((float)0.8, (float)3.5);
  private final static BodyImage houndImageRight = new BodyImage("assets/images/hound/hound-right.gif", 5);
  private final static BodyImage houndImageLeft = new BodyImage("assets/images/hound/hound-left.gif", 5);

  public Hound(GameWorld world, int leftBoundary, int rightBoundary) {
    super(world, houndShape, houndImageRight, houndImageLeft, leftBoundary, rightBoundary, 5);
  }
  
}
