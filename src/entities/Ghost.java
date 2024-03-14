package entities;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import projectiles.Fireball;

public class Ghost extends Enemy implements StepListener{
  
  private static final Shape ghostShape = new BoxShape(1.5f, 4.5f);
  private static final BodyImage ghostImage = new BodyImage("./assets/images/ghost/ghost.gif", 5.1f);
  private static final float FIRE_RANGE = 60.0f; // The range at which the ghost will start shooting.
  private static final float FIREBALL_SPEED = -10.0f; // Negative for left
  private float timeSinceLastShot = 0.0f;
  private Player player;
  private World world;
    public Ghost(World world, Player player) {
        super(world, ghostShape, null, null, 0, 0, 0);
        addImage(ghostImage);
        this.world.addStepListener(this);
    }
    @Override
    public void postStep(StepEvent e) {
      // N.A
    }
    @Override
    public void preStep(StepEvent e) {
      timeSinceLastShot += e.getStep();
      Vec2 playerPosition = player.getPosition();
      Vec2 ghostPosition = getPosition();
      if (Math.abs(ghostPosition.x - playerPosition.x) < FIRE_RANGE && timeSinceLastShot >= 0.5f) {
          shootFireball();
          timeSinceLastShot = 0.0f; // Reset the timer after shooting
      }
    }

    private void shootFireball(){
      Vec2 position = this.getPosition();
      Fireball fireball = new Fireball(this.getWorld(), position);
      fireball.setPosition(position.add(new Vec2(-1.5f, 0))); // Start the fireball slightly to the left
      fireball.setLinearVelocity(new Vec2(FIREBALL_SPEED, 0)); // Shoot left
      fireball.setGravityScale(0);
    }
    
}
