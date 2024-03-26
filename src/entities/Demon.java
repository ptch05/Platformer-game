package entities;
import city.cs.engine.*;

public class Demon extends Enemy{
  private int hitsTaken;
  private boolean isDemonAttacking = false;
  
  private static final Shape demonShape = new BoxShape(1.5f, 4.5f);
  private static BodyImage demonImage;
  private Player player;
  
  public Demon(World world, Player player) {
    super(world, demonShape, demonImage, demonImage, 0, 0, 0);
    addImage(demonImage);
    this.player = player;
  }

  public boolean isDemonAttacking(){
    return isDemonAttacking;
  }

  

  public void demonAttack(){
    isDemonAttacking = true;
    if(!isDemonAttacking()){
      demonImage = new BodyImage("./assets/images/ghost/ghost.gif", 7f);
    }
  }

  public void endDemonAttack(){
    isDemonAttacking = false;
    if(isDemonAttacking()){
      demonImage = new BodyImage("null", 7f);
    }
  }

  public void hitByAttack(boolean isSpecialAttack) {
    if (isSpecialAttack) {
      hitsTaken+=2; 
    } else {
      hitsTaken++;
    }

    if (hitsTaken >= 5) {
      enemyDie(); 
    }
  }
  
   
}
