package entities;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import main.GameWorld;

public class Skeleton extends Walker{
  private static final Shape skeletonShape = new BoxShape((float)0.8, (float)2.8);
  private static final BodyImage skeletonImageRight = new BodyImage("assets/images/skeleton/skeleton-clothed-right.gif", 6);
  private static final BodyImage skeletonImageLeft = new BodyImage("assets/images/skeleton/skeleton-clothed-left.gif", 6);

  private float patrolLeftBoundary;
  private float patrolRightBoundary;
  private final int walkingSpeed = 7;
  private boolean movingRight;

  public Skeleton(GameWorld world, float leftBoundary, float rightBoundary) {
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

  public void skeletonDie(){
    this.destroy();

  }

}
