package interactables;

import city.cs.engine.BodyImage;
import city.cs.engine.BoxShape;
import city.cs.engine.Shape;
import city.cs.engine.StaticBody;
import city.cs.engine.World;

public class Spikes extends StaticBody {
  private static final Shape spikesShape = new BoxShape(4, 2f);
  private static final BodyImage spikesImage = new BodyImage("assets/images/level-data/ground-spikes.png", 4f);

  public Spikes(World world) {
    super(world, spikesShape);
    addImage(spikesImage);
  }

}