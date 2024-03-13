package entities;

import org.jbox2d.common.Vec2;
import city.cs.engine.*;
import effects.DeathAnimation;

public class Enemy extends Walker{
  protected Shape shape;
  protected BodyImage imageRight;
  protected BodyImage imageLeft;

  private final int patrolLeftBoundary;
  private final int patrolRightBoundary;
  private final int walkingSpeed;
  private boolean movingRight;

  public Enemy(World world, Shape shape, BodyImage imageRight, BodyImage imageLeft, int leftBoundary, int rightBoundary, int walkingSpeed) {
    super(world, shape);
    this.shape = shape;
    this.imageRight = imageRight;
    this.imageLeft = imageLeft;
    this.patrolLeftBoundary = leftBoundary;
    this.patrolRightBoundary = rightBoundary;
    this.walkingSpeed = walkingSpeed;
    this.movingRight = true;
    updateImage();
  }

  public void patrol() {
    if (this.movingRight && this.getPosition().x >= patrolRightBoundary) {
        this.movingRight = false;
    } else if (!this.movingRight && this.getPosition().x <= patrolLeftBoundary) {
        this.movingRight = true;
    }

    this.setLinearVelocity(new Vec2(this.movingRight ? walkingSpeed : -walkingSpeed, 0)); // Move the enemy at a constant speed left or right
    updateImage();
  }

  private void updateImage() {
    this.removeAllImages(); // Remove all current images
    BodyImage newImage = this.movingRight ? this.imageRight : this.imageLeft;
    this.addImage(newImage); // Add the correct image based on the current direction
}

  public void enemyDie() {
        // Position where the enemy died
        Vec2 position = this.getPosition();
        DeathAnimation deathAnimation = new DeathAnimation(this.getWorld(), position, new BodyImage("./assets/images/misc/enemy-death.gif", 4));
        deathAnimation.setPosition(position);
        this.destroy(); //Destroys the actual enemy itself
  }

}