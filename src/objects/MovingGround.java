package objects;

import org.jbox2d.common.Vec2;
import city.cs.engine.*;

/**
 * A moving ground platform that oscillates between two horizontal boundaries within the game world.
 * This class extends StaticBody and implements the StepListener interface to update the platform's
 * position at each simulation step, creating a dynamic obstacle or support for the player.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class MovingGround extends StaticBody implements StepListener {
    private static final Shape movingGroundShape = new BoxShape(3f, 1.5f);
    private static final BodyImage movingGroundImage = new BodyImage("./assets/images/level-data/level2/moving-ground.png", 4f);
  
    private Vec2 startPosition;
    private float leftBoundary, rightBoundary;
    private float delta;

    public MovingGround(World world, float leftBoundary, float rightBoundary, float speed) {
        super(world, movingGroundShape);
        startPosition = this.getPosition();
        addImage(movingGroundImage);
        this.leftBoundary = startPosition.x + leftBoundary;
        this.rightBoundary = startPosition.x + rightBoundary;
        this.delta = speed;
        world.addStepListener(this);
    }

    /**
     * Called before each physics step. Updates the platform's position, reversing its direction
     * when it reaches one of its horizontal boundaries.
     *
     * @param stepEvent The event object containing information about the simulation step.
     */
    @Override
    public void preStep(StepEvent stepEvent) {
        Vec2 currentPosition = this.getPosition();
        if (currentPosition.x <= leftBoundary){
            delta = Math.abs(delta); // Move right
        } else if (currentPosition.x >= rightBoundary){
            delta = -Math.abs(delta); // Move left
        }
        this.setPosition(new Vec2(currentPosition.x + delta, currentPosition.y));
    }

    @Override
    public void postStep(StepEvent stepEvent) {
        // N.A
    }
}

