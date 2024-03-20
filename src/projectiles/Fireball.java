package projectiles;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;
public class Fireball extends DynamicBody {

  private static final Shape fireballShape = new BoxShape(0.25f, 0.5f);
  private static final BodyImage fireballImage = new BodyImage("./assets/images/ghost/fireball.gif", 5.5f);

  public Fireball(World world, Vec2 position) {
    super(world, fireballShape);
    addImage(fireballImage);
  }
  
}
