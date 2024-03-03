package entities;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import effects.DeathAnimation;
import main.GameWorld;

public class Skeleton extends Walker{
  private static final Shape skeletonShape = new BoxShape((float)0.8, (float)2.8);
  private static final BodyImage skeletonImageRight = new BodyImage("assets/images/skeleton/skeleton-clothed-right.gif", 6);
  private static final BodyImage skeletonImageLeft = new BodyImage("assets/images/skeleton/skeleton-clothed-left.gif", 6);

  private int patrolLeftBoundary;
  private int patrolRightBoundary;
  private final int walkingSpeed = 7;
  private boolean movingRight;

  public Skeleton(GameWorld world, int leftBoundary, int rightBoundary) {
    super(world, skeletonShape);
    this.patrolLeftBoundary = leftBoundary;
    this.patrolRightBoundary = rightBoundary;
    this.movingRight = true;
    updateImage();
  }

  public void patrol() {
    if (this.movingRight && this.getPosition().x >= patrolRightBoundary) {
        this.movingRight = false;
    } else if (!this.movingRight && this.getPosition().x <= patrolLeftBoundary) {
        this.movingRight = true;
    }

    this.setLinearVelocity(new Vec2(this.movingRight ? 2 : -2, 0)); // Move the skeleton at a constant speed
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
    this.addImage(this.movingRight ? skeletonImageRight : skeletonImageLeft);
  }

  public void skeletonDie() {
        // Position where the skeleton died
        Vec2 position = this.getPosition();
        DeathAnimation deathAnimation = new DeathAnimation(this.getWorld(), position, new BodyImage("assets/images/misc/enemy-death.gif", 4));
        deathAnimation.setPosition(position);
        this.destroy(); //Destroys the actual skeleton itself
    }

}
