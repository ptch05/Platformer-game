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
  private final int numberOfFireTraps = 7;
  private final float[] fireTrapXPos = new float[]{185, 280, 320, 377, 415, 590, 700};
  private final float[] fireTrapYPos = new float[]{-5.2f, -5.2f, -5.2f, -25.65f, 10.3f, 10.3f, -14.65f};

  public Level3(Game game) {
    super(game);
    numberOfSkeletons = 4;
    numberOfHounds = 6;
    numberOfGhosts = 6;
    numberOfPotions = 2;
    skeletonPatrolLeftBoundary = new int[] {145, 360, 460, 595}; 
    skeletonPatrolRightBoundary = new int[]{155, 375, 480, 615};
    houndPatrolLeftBoundary = new int[]{230, 290, 422, 555, (int) 638.5, 715};
    houndPatrolRightBoundary = new int[]{250, 310, 435, 575, 650, 735};
    skeletonPositions = new Vec2[]{
      new Vec2(150, -7.25f),
      new Vec2(372.5f, -27f),
      new Vec2(470, 7f),
      new Vec2(610, 7f)
    };
    houndPositions = new Vec2[]{
      new Vec2(240, -7f),
      new Vec2(300, -7f),
      new Vec2(430, 8.5f),
      new Vec2(565, 8.5f),
      new Vec2(640, 8.5f),
      new Vec2(725, -18.5f)
    };
    ghostPositions = new Vec2[]{
      new Vec2(180, -7.5f),
      new Vec2(265, -7.5f),
      new Vec2(405, 6.5f),
      new Vec2(450, 6.5f),
      new Vec2(630, 6.5f),
      new Vec2(750, -15f)
    };
    potionXPos = new float[]{300, 385 };
    potionYPos = new float[]{-8.5f, -29f};
    
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
      armour.setPosition(new Vec2(580,7.05f));
      trophy = new Trophy(this);
      trophy.setPosition(new Vec2(840,0));
      demon = new Demon(this, player);
      demon.setPosition(new Vec2(820, -4));
  }

  public void createEnvironment() {
    // Logic for the third level
    addLava(10);
    addGround(10);   
    addLava(2); 
    addGround(15);
    
    //Dead end code
    YPos = -13.5f;
    for(int i=0; i<4; i++){
      YPos -= 5f;
      addGround(1);
    }
    addGround(8);
    addLava(4);

    //Continue from here
    XPos = 360;
    YPos = -13.5f;
    for(int i=0; i<4; i++){
      YPos += 4f;
      addGround(1);
    }
    addGround(15);
    XPos = 510;
    YPos += 5.5f;
    addGround(1);
    YPos -= 5.5f;
    XPos = 493;
    addLava(4);
    addGround(15);
    for(int i=0; i<5; i++){
      YPos -= 5f;
      addGround(1);
    }
    addGround(11);
    for(int i=0; i<2; i++){
      YPos += 4f;
      addGround(1);
    }
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
