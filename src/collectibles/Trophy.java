package collectibles;

import city.cs.engine.*;
public class Trophy extends StaticBody {

  private static final Shape trophyShape = new BoxShape((float)0.1, (float)0.02);
  private static final BodyImage trophyImage = new BodyImage("./assets/images/collectibles/trophy.png", (float) 2.5);
  public Trophy(World world) {
    super(world, trophyShape);
    addImage(trophyImage);
  }
  
}
