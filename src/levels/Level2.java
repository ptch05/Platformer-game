package levels;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import main.Game;
import objects.Armour;
import objects.Potion;
import objects.Trophy;

public class Level2 extends GameLevel{

  public Level2(Game game) {
    super(game);
    //numberOfSkeletons = 5;
    //numberOfHounds = 6;
    //numberOfGhosts = 4;
    numberOfSkeletons = 0;
    numberOfHounds = 0;
    numberOfGhosts = 0;
    numberOfPotions = 0;
    skeletonPatrolLeftBoundary = new int[] {}; 
    skeletonPatrolRightBoundary = new int[]{};
    houndPatrolLeftBoundary = new int[]{};
    houndPatrolRightBoundary = new int[]{};
    skeletonPositions = new Vec2[]{
    };
    houndPositions = new Vec2[]{
    };
    ghostPositions = new Vec2[]{
    };
    potionXPos = new float[]{};
    potionYPos = new float[]{};
  
    player.setPosition(new Vec2(200,-6));
    armour = new Armour(this);
    armour.setPosition(new Vec2(500,-6));
    trophy = new Trophy(this);
    trophy.setPosition(new Vec2(100,0));
    AudioHandler.playLevel2Music();
    initializeWorld();
  }

  protected void initializeWorld() { 
      //Uses this method to make the world every time
      XPos = 100; // Resets X position for ground creation
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
    YPos = -10f;
    addSpikes(14);
    YPos = -9f;
    addGround(15);
  }

  @Override
  public String getLevelName() {
      return "Level2";
  }

  @Override
  public boolean isComplete() {
      return getPlayer().getKillCounter() > 15;
  }

}
