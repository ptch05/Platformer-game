package entities;

import city.cs.engine.BodyImage;
import city.cs.engine.BoxShape;
import city.cs.engine.Shape;
import main.GameWorld;

public class Ghost extends Enemy {
  
  private static final Shape ghostShape = new BoxShape((float)1.5, (float)4.5);
  private final static BodyImage ghostImage = new BodyImage("./assets/images/ghost/ghost.gif", (float) 5.1);
  public Ghost(GameWorld world, Shape shape, BodyImage imageRight, BodyImage imageLeft, int leftBoundary,
      int rightBoundary, int walkingSpeed) {
    super(world, shape, imageRight, imageLeft, leftBoundary, rightBoundary, walkingSpeed);
    //TODO Auto-generated constructor stub
  }

}
