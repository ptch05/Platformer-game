package objects;

import city.cs.engine.*;
import levels.GameLevel;

public class Spikes extends StaticBody {
  private static Shape spikesShape = new BoxShape(4, 2f);
  public Spikes(World world, GameLevel currentLevelName) {
        super(world, spikesShape);
        BodyImage spikesImage;
        String levelName = currentLevelName.getLevelName(); 
        
        if ("Level1".equals(levelName)) {
            spikesImage = new BodyImage("./assets/images/level-data/level1/ground-spikes.png", 4f);
        } else if ("Level2".equals(levelName)) {
            spikesImage = new BodyImage("./assets/images/level-data/level2/ground-spikes.png", 3f);
            spikesShape = new BoxShape(4,1.2f);
        } else {
            throw new IllegalArgumentException("Unsupported level: " + levelName);
        }
        addImage(spikesImage);
    }
}
