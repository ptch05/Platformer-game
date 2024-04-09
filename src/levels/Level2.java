package levels;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import collectibles.Armour;
import collectibles.Potion;
import collectibles.Trophy;
import main.Game;
import objects.MovingGround;

/**
 * Level2 introduces more complexity with additional enemies including ghosts,
 * more hounds, and skeletons. It also features moving ground platforms and
 * requires the player to navigate through more challenging terrain.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class Level2 extends GameLevel{
  public Level2(Game game) {
    super(game);
    numberOfSkeletons = 5;
    numberOfHounds = 6;
    numberOfGhosts = 4;
    numberOfPotions = 2;
    skeletonPatrolLeftBoundary = new int[] {225, 260, 650, 740, 885}; 
    skeletonPatrolRightBoundary = new int[]{265, 285, 675, 770, 920};
    houndPatrolLeftBoundary = new int[]{165, 365, 405, 590, 795, 850};
    houndPatrolRightBoundary = new int[]{210, 390, 425, 610, 825, 875};
    skeletonPositions = new Vec2[]{
      new Vec2(240, -3f),
      new Vec2(270, -3f),
      new Vec2(665, 8f),
      new Vec2(755, -4.5f),
      new Vec2(900, 9f)
    };
    houndPositions = new Vec2[]{
      new Vec2(180, -2.5f),
      new Vec2(370, -2.5f),
      new Vec2(395, -2.5f),
      new Vec2(605, 5.5f),
      new Vec2(810, -3.5f),
      new Vec2(860, 10f)
    };
    ghostPositions = new Vec2[]{
      new Vec2(220, -2f),
      new Vec2(345, -2f),
      new Vec2(640, 7f),
      new Vec2(785, -3f)
    };
    potionXPos = new float[]{250, 820};
    potionYPos = new float[]{-4.3f, -5.85f};
  
    player.setPosition(new Vec2(200,-6));
    AudioHandler.playLevel2Music();
    initializeWorld();
  }

  @Override
  protected void initializeWorld() { 
    //Uses this method to make the world every time
    XPos = 100; // Resets X position for ground creation
    YPos = -10f;
    createEnvironment();
    addSkeletons();
    addHounds();
    addGhosts();

    for(int i=0; i<numberOfPotions; i++){
        Potion potion = new Potion(this);
        potion.setPosition(new Vec2(potionXPos[i], potionYPos[i]));
    }

    armour = new Armour(this);
    armour.setPosition(new Vec2(770,-5.65f));
    trophy = new Trophy(this);
    trophy.setPosition(new Vec2(955,18f));
  }

  public void createEnvironment() {
    // Logic for the second level
    addSpikes(14);
    addGround(20);
    addSpikes(9);
    addGround(1);
    addSpikes(5);
    addGround(15);
    for (int i = 0; i < 2; i++) {
      YPos+=4.5f;
      addGround(1);
    }
    YPos -=4f;
    addSpikes(35);
    XPos = 480;
    addMovingGround(470, 518, 0.2f);
    XPos = 540;
    addMovingGround(525, 567, 0.2f);
    YPos += 7f;
    XPos = 585;
    addGround(18);
    for (int i = 0; i < 3; i++) {
      YPos-=4.5f;
      addGround(1);
    }
    addGround(15);
    for (int i = 0; i < 3; i++) {
      YPos+=4.5f;
      addGround(1);
    }
    addGround(15);
    addSpikes(14);
  }

  private void addMovingGround(int leftBoundary, int rightBoundary, float speed){
    MovingGround movingGround = new MovingGround(this, leftBoundary, rightBoundary, speed);
    movingGround.setPosition(new Vec2(XPos, YPos+8.5f));
  }

  @Override
  public String getLevelName() {
      return "Level2";
  }

  @Override
  public boolean isComplete() {
      return getPlayer().getKillCounter() > 13;
  }

}
