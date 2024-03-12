package projectiles;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import main.GameWorld;

public class Fireball extends DynamicBody {

  private static final Shape fireballShape = new BoxShape(2f, 0.5f);
  private static final BodyImage fireballImage = new BodyImage("./assets/images/misc/fireball.gif", 1f);

  public Fireball(World world, Vec2 position) {
    super(world, fireballShape);
  }
  
}
