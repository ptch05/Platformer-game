package entities;

import org.jbox2d.common.Vec2;
import city.cs.engine.*;
import effects.DeathAnimation;
import main.GameWorld;

public class Enemy extends Walker{
  protected Shape shape;
  protected BodyImage imageRight;
  protected BodyImage imageLeft;

  private final int patrolLeftBoundary;
  private final int patrolRightBoundary;
  private final int walkingSpeed;
  private boolean movingRight;

  public Enemy(GameWorld world, Shape shape, BodyImage imageRight, BodyImage imageLeft, int leftBoundary, int rightBoundary, int walkingSpeed) {
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

    this.setLinearVelocity(new Vec2(this.movingRight ? 2 : -2, 0)); // Move the enemy at a constant speed
    updateImage();
  }

  public void reverseDirection() {
    if (getLinearVelocity().x > 0) {
        startWalking(-walkingSpeed);
    } else {
        startWalking(walkingSpeed);
    }
  }

  private void updateImage() {
    this.removeAllImages();
    this.addImage(this.movingRight ? this.imageRight : this.imageLeft);
}

  public void enemyDie() {
        // Position where the enemy died
        Vec2 position = this.getPosition();
        DeathAnimation deathAnimation = new DeathAnimation(this.getWorld(), position, new BodyImage("./assets/images/misc/enemy-death.gif", 4));
        deathAnimation.setPosition(position);
        this.destroy(); //Destroys the actual enemy itself
    }

}