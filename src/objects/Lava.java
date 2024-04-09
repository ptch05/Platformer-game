package objects;

import city.cs.engine.*;

/**
 * Represents a lava trap in the game world. Lava is a static body that
 * kills player upon contact with the player.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class Lava extends StaticBody {
  private static final Shape lavaShape = new BoxShape(4, 0.0000001f);
  private static final BodyImage lavaImage = new BodyImage("./assets/images/level-data/level3/lava.gif", 10f);
  public Lava(World world) {
    super(world, lavaShape);
    addImage(lavaImage);
  }
}
