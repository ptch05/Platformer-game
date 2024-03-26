package objects;

import city.cs.engine.*;

public class Lava extends StaticBody {
  private static final Shape lavaShape = new BoxShape(4, 0.0000001f);
  private static final BodyImage lavaImage = new BodyImage("./assets/images/level-data/level3/lava.gif", 10f);
  public Lava(World world) {
        super(world, lavaShape);
        addImage(lavaImage);
    }
}
