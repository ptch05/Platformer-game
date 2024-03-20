package entities;
import city.cs.engine.*;
import utilities.GhostListener;

public class Ghost extends Enemy{
  private boolean isGhostAlive = true;
  private int hitsTaken;
  
  private static final Shape ghostShape = new BoxShape(1.5f, 4.5f);
  private static final BodyImage ghostImage = new BodyImage("./assets/images/ghost/ghost.gif", 7f);
  private Player player;
  
  public Ghost(World world, Player player) {
    super(world, ghostShape, ghostImage, ghostImage, 0, 0, 0);
    addImage(ghostImage);
    this.player = player;
    GhostListener listener = new GhostListener(this, player);
    world.addStepListener(listener);
  }

  public boolean isGhostAlive(){
    return isGhostAlive;
  }

  public void setGhostDead(){
    isGhostAlive = false;
  }

  public void hitByAttack(boolean isSpecialAttack) {
    if (isSpecialAttack) {
      setGhostDead(); // Special attack kills immediately
    } else {
      hitsTaken++;
      if (hitsTaken >= 2) {
        setGhostDead(); // Normal attack kills after 2 hits
      }
    }
  }
   
}
