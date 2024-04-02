package entities;
import audio.AudioHandler;
import city.cs.engine.*;
import utilities.DemonListener;

public class Demon extends Enemy{
  private int hitsTaken;
  private boolean isDemonAttacking = false;
  private boolean isDemonAlive = true;
  
  private static final Shape demonShape = new BoxShape(2.8f, 3f);
  private static final Shape demonAttackShape = new BoxShape(5.75f,3f);
  private static BodyImage demonIdleImage = new BodyImage("./assets/images/demon/demon-idle.gif", 12.5f);
  private static BodyImage demonAttackImage = new BodyImage("./assets/images/demon/demon-attack.gif", 14f);
  private Player player;
  private Fixture demonAttackFixture;
  
  public Demon(World world, Player player) {
    super(world, demonShape, demonIdleImage, demonIdleImage, 0, 0, 0);
    addImage(demonIdleImage);
    this.player = player;
    DemonListener listener = new DemonListener(this, player);
    world.addStepListener(listener);
    this.setGravityScale(0);
  }

  public boolean isDemonAttacking(){
    return isDemonAttacking;
  }

  public void demonAttack(){
    isDemonAttacking = true;
    removeAllImages();
    addImage(demonAttackImage);
    if (demonAttackFixture != null) {
      demonAttackFixture.destroy();
    }
    demonAttackFixture = new SolidFixture(this, demonAttackShape); 
    AudioHandler.playDemonAttackSound();
  }


  public void endDemonAttack(){
    isDemonAttacking = false;
    removeAllImages();
    addImage(demonIdleImage);
    if (demonAttackFixture != null) {
      demonAttackFixture.destroy(); // Remove the attack hitbox using destroy()
      demonAttackFixture = new SolidFixture(this, demonShape);
    }
  }

  public int getHitsTaken(){
    return hitsTaken;
  }

  public boolean isDemonAlive(){
    return isDemonAlive;
  }

  public void hitByAttack(boolean isSpecialAttack) {
    if (isSpecialAttack) {
      hitsTaken++; 
    } else {
      hitsTaken++;
      if (hitsTaken >= 10) {
        isDemonAlive = false;
        AudioHandler.stopDemonAttackSound();
        AudioHandler.stopDemonIdleSound();
      }
    }
  }
}
