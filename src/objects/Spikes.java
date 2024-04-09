package objects;

import city.cs.engine.*;
import levels.GameLevel;

/**
 * Represents spike hazards on the ground within the game world. Spikes are static bodies
 * that kills the player on contact. This class allows for customization based on
 * the level they are placed in.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class Spikes extends StaticBody {
    private static Shape spikesShape = new BoxShape(4, 2f);
    /**
     * Constructs spikes in the game world with a level-specific appearance.
     *
     * @param world          The game world where the spikes are placed.
     * @param currentLevelName The current game level to determine the appropriate spike appearance.
     */
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
