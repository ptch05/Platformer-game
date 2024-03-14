package levels;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import main.Game;
import objects.Armour;
import objects.Potion;
import objects.Trophy;

public class Level1 extends GameLevel{
  public Level1(Game game) {
    
    super(game);
    numberOfSkeletons = 7;
    numberOfHounds = 2;
    numberOfGhosts = 3;
    numberOfPotions = 2;
    skeletonPatrolLeftBoundary = new int[] {25, 52, 91, 160,235,315, 380}; 
    skeletonPatrolRightBoundary = new int[]{35,75, 100, 175,245,335, 400};
    houndPatrolLeftBoundary = new int[]{0,350};
    houndPatrolRightBoundary = new int[]{20,370};
    XPos = -68.15f;
    YPos = -13f;
    skeletonPositions = new Vec2[]{
      new Vec2(30, -7.3f),
      new Vec2(60, -7.3f),
      new Vec2(95, 1f),
      new Vec2(170, -3),
      new Vec2(240, 15),
      new Vec2(325, 22),
      new Vec2(390, 22)
    };
    houndPositions = new Vec2[]{
      new Vec2(10, -5.8f),
      new Vec2(352, 20)
    };
    ghostPositions = new Vec2[]{
      new Vec2(10, 0f),
      new Vec2(352, 20),
      new Vec2(170,-3)
    };
    potionXPos = new float[]{60, 175};
    potionYPos = new float[]{-6, -1};
    
  
    player.setPosition(new Vec2(-3, -5));
    armour = new Armour(this);
    armour.setPosition(new Vec2(230, 17));
    trophy = new Trophy(this);
    trophy.setPosition(new Vec2(410,27));
    AudioHandler.playLevel1Music();
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
    
  }

  @Override
  public String getLevelName() {
    return "Level 1";
  }

  

}

