package utilities;

import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import entities.Enemy;
/**
 * A step listener dedicated to controlling the patrolling behavior of an Enemy object.
 * It invokes the patrol method of the enemy at each simulation step before any collision
 * response is calculated.
 * 
 * This ensures that the enemy's patrolling logic is consistently evaluated, allowing for
 * movement and boundary checks in the game world.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */

public class PatrolListener implements StepListener {
    private Enemy enemy;

    /**
     * Constructs a patrol listener with the specified enemy.
     *
     * @param enemy The enemy object whose patrolling behavior this listener will control.
     */
    public PatrolListener(Enemy enemy){ 
        this.enemy = enemy;
    }

    /**
     * Invoked before the simulation step is processed.
     * It calls the patrol method of the enemy to handle its movement.
     * 
     * @param e The event object containing information about the simulation step.
     */
    @Override
    public void preStep(StepEvent e) {
        enemy.patrol();
    }

    /**
     * Invoked after the simulation step is processed.
     * This method is not utilized but is required by the StepListener interface.
     * 
     * @param e The event object containing information about the simulation step.
     */
    @Override
    public void postStep(StepEvent e) {
        // This method is not used.
    }

}
