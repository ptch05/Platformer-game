package objects;

import city.cs.engine.*;

public class Spikes extends StaticBody {
  private static final Shape spikesShape = new BoxShape(4, 2f);
  private static final BodyImage spikesImage = new BodyImage("./assets/images/level-data/ground-spikes.png", 4f);

  public Spikes(World world) {
    super(world, spikesShape);
    addImage(spikesImage);
  }

}
