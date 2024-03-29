package levels;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import collectibles.Armour;
import collectibles.Potion;
import collectibles.Trophy;
import entities.Demon;
import main.Game;
import objects.FireTrap;
import objects.Lava;

public class Level3 extends GameLevel{
  private final int numberOfFireTraps = 1;
  private final float[] fireTrapXPos = new float[]{185};
  private final float[] fireTrapYPos = new float[]{-5.2f};

  public Level3(Game game) {
    super(game);
    numberOfSkeletons = 1;
    numberOfHounds = 0;
    numberOfGhosts = 0;
    numberOfPotions = 0;
    skeletonPatrolLeftBoundary = new int[] {145}; 
    skeletonPatrolRightBoundary = new int[]{155};
    houndPatrolLeftBoundary = new int[]{};
    houndPatrolRightBoundary = new int[]{};
    skeletonPositions = new Vec2[]{
      new Vec2(150, -7.25f)
    };
    houndPositions = new Vec2[]{
    };
    ghostPositions = new Vec2[]{
    };
    potionXPos = new float[]{};
    potionYPos = new float[]{};
    
    AudioHandler.playLevel3Music();
    initializeWorld();
  }

  
  @Override
  protected void initializeWorld() { 
      //Uses this method to make the world every time
      XPos = 0; // Resets X position for ground creation
      YPos = -13f; // Resets Y position for ground creation
      createEnvironment();

      addSkeletons();
      addHounds();
      addGhosts();

      for(int i=0; i<numberOfPotions; i++){
          Potion potion = new Potion(this);
          potion.setPosition(new Vec2(potionXPos[i], potionYPos[i]));
      }

      for(int i=0; i<numberOfFireTraps; i++){
        FireTrap fireTrap = new FireTrap(this, player);
        fireTrap.setPosition(new Vec2(fireTrapXPos[i], fireTrapYPos[i]));
      }

      armour = new Armour(this);
      armour.setPosition(new Vec2(1000,0));
      trophy = new Trophy(this);
      trophy.setPosition(new Vec2(1700,0));
      demon = new Demon(this, player);
      demon.setPosition(new Vec2(1650, 2));
  }

  public void createEnvironment() {
    // Logic for the third level
    addLava(10);
    addGround(10);   
    addLava(2); 
    addGround(10);

  }

  @Override
  public String getLevelName() {
      return "Level3";
  }

  @Override
  public boolean isComplete() {
      return getPlayer().getKillCounter() > 14;
  }

  private void createLava(){
      XPos+=5.8f;
      Lava lava = new Lava(this);
      lava.setPosition(new Vec2(XPos, YPos-0.75f));
      XPos+=6.4f;
  }
  
  private void addLava(int times) {
    //Adds in lava every time it's called
    for (int i = 0; i < times; i++) {
        createLava();
    }
    XPos+=6.3f;
  }


}
