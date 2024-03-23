package levels;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import main.Game;
import objects.Armour;
import objects.Potion;
import objects.Trophy;

public class Level3 extends GameLevel{

  public Level3(Game game) {
    super(game);
    numberOfSkeletons = 0;
    numberOfHounds = 0;
    numberOfGhosts = 0;
    numberOfPotions = 0;
    skeletonPatrolLeftBoundary = new int[] {}; 
    skeletonPatrolRightBoundary = new int[]{};
    houndPatrolLeftBoundary = new int[]{};
    houndPatrolRightBoundary = new int[]{};
    XPos = -68.15f;
    YPos = -13f;
    skeletonPositions = new Vec2[]{
    };
    houndPositions = new Vec2[]{
    };
    ghostPositions = new Vec2[]{
    };
    potionXPos = new float[]{};
    potionYPos = new float[]{};
    
  
    player.setPosition(new Vec2());
    armour = new Armour(this);
    armour.setPosition(new Vec2());
    trophy = new Trophy(this);
    trophy.setPosition(new Vec2(1000,0));
    AudioHandler.playLevel3Music();
    initializeWorld();
  }

  protected void initializeWorld() { 
      //Uses this method to make the world every time
      XPos = -68.15f; // Resets X position for ground creation
      YPos = -13f; // Resets Y position for ground creation
      createEnvironment();

      addSkeletons();
      addHounds();
      addGhosts();

      for(int i=0; i<numberOfPotions; i++){
          Potion potion = new Potion(this);
          potion.setPosition(new Vec2(potionXPos[i], potionYPos[i]));
      }
  }

  public void createEnvironment() {
    // Logic for the second level

  }

  @Override
  public String getLevelName() {
      return "Level3";
  }

  @Override
  public boolean isComplete() {
      return getPlayer().getKillCounter() > 15;
  }

}
