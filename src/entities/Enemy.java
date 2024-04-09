package entities;

import org.jbox2d.common.Vec2;
import city.cs.engine.*;
import effects.DeathAnimation;

/**
 * Abstract representation of an enemy character in the game.
 * The enemy has the ability to patrol between two boundaries
 * and change direction upon reaching them.
 * Upon death, a death animation is triggered.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */

public class Enemy extends Walker{
  protected Shape shape;
  protected BodyImage imageRight;
  protected BodyImage imageLeft;

  private final int patrolLeftBoundary;
  private final int patrolRightBoundary;
  private final int walkingSpeed;
  private boolean movingRight;

  /**
   * Constructs an Enemy with defined boundaries for patrolling and images for display.
   * 
   * @param world The world this enemy belongs to.
   * @param shape The physical shape of the enemy.
   * @param imageRight The image to display when the enemy moves to the right.
   * @param imageLeft The image to display when the enemy moves to the left.
   * @param leftBoundary The left boundary of the enemy's patrol area.
   * @param rightBoundary The right boundary of the enemy's patrol area.
   * @param walkingSpeed The speed at which the enemy patrols.
   */

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

  /**
   * Controls the enemy's patrolling behavior, making it move left and right between its boundaries.
   */
  public void patrol() {
    if (this.movingRight && this.getPosition().x >= patrolRightBoundary) {
        this.movingRight = false;
    } else if (!this.movingRight && this.getPosition().x <= patrolLeftBoundary) {
        this.movingRight = true;
    }

    this.setLinearVelocity(new Vec2(this.movingRight ? walkingSpeed : -walkingSpeed, 0)); // Move the enemy at a constant speed left or right
    updateImage();
  }

  /**
   * Updates the enemy's image based on the current direction of movement.
   */
  private void updateImage() {
    this.removeAllImages(); // Remove all current images
    BodyImage newImage = this.movingRight ? this.imageRight : this.imageLeft;
    this.addImage(newImage); // Add the correct image based on the current direction
  }

  /**
   * Triggers the enemy's death sequence, creating a death animation at the enemy's position
   * and removing the enemy from the world.
   */
  public void enemyDie() {
        // Position where the enemy died
        Vec2 position = this.getPosition();
        DeathAnimation deathAnimation = new DeathAnimation(this.getWorld(), position, new BodyImage("./assets/images/misc/enemy-death.gif", 4));
        deathAnimation.setPosition(position);
        this.destroy(); //Destroys the actual enemy itself
  }

}