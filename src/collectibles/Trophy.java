package collectibles;

import city.cs.engine.*;
public class Trophy extends StaticBody {
  /**
   * Represents a trophy collectible in the game. The trophy is an interactive
   * static body that allows the player to progress to the next level.
   * 
   * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
   * @version 1.0
   * @since 1.0
   */

  /**
   * The shape of the trophy collectible, represented as a small box.
   */
  private static final Shape trophyShape = new BoxShape((float)0.1, (float)0.02);

  /**
   * The image for the trophy collectible.
   */
  private static final BodyImage trophyImage = new BodyImage("./assets/images/collectibles/trophy.png", (float) 2.5);

  /**
   * Creates a trophy collectible in the game world.
   * The trophy has a defined shape and image that is added to it upon creation.
   * 
   * @param world the game world where the trophy will exist
   */
  public Trophy(World world) {
    super(world, trophyShape);
    addImage(trophyImage);
  }
  
}
