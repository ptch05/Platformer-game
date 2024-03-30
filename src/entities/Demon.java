package entities;
import audio.AudioHandler;
import city.cs.engine.*;
import utilities.DemonListener;

public class Demon extends Enemy{
  private int hitsTaken;
  private boolean isDemonAttacking = false;
  
  private static final Shape demonShape = new BoxShape(2.8f, 3f);
  private static final Shape demonAttackShape = new BoxShape(5f,3f);
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
    createAttackHitbox(demonAttackShape);
    AudioHandler.playDemonAttackSound();
  }

  private void createAttackHitbox(Shape demonAttackShape) {
    demonAttackFixture = new SolidFixture(this, demonAttackShape); // Create a new attack hitbox
  }

  public void endDemonAttack(){
    isDemonAttacking = false;
    removeAllImages();
    addImage(demonIdleImage);
    if (demonAttackFixture != null) {
      demonAttackFixture.destroy(); // Remove the attack hitbox using destroy()
      demonAttackFixture = null; // Clear the reference after destruction
    }
  }

  public void hitByAttack(boolean isSpecialAttack) {
    if (isSpecialAttack) {
      hitsTaken+=2; 
    } else {
      hitsTaken++;
    }
    if (hitsTaken >= 12) { //This makes it so it takes 6 hits to kill (every attack counts for 2 hits in this engine)
      enemyDie(); 
      AudioHandler.stopDemonIdleSound();
    }
  }   
}
