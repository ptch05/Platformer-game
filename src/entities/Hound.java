package entities;

import city.cs.engine.BodyImage;
import city.cs.engine.BoxShape;
import city.cs.engine.Shape;
import main.GameWorld;

public class Hound extends Enemy {
  private static final Shape houndShape = new BoxShape((float)1.5, (float)3.05);
  private final static BodyImage houndImageRight = new BodyImage("./assets/images/hound/hound-right.gif", (float) 5.1);
  private final static BodyImage houndImageLeft = new BodyImage("./assets/images/hound/hound-left.gif", (float) 5.1);

  public Hound(GameWorld world, int leftBoundary, int rightBoundary) {
    super(world, houndShape, houndImageRight, houndImageLeft, leftBoundary, rightBoundary, 8);
  }
  
}
