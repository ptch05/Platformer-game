package utilities;

import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import entities.Enemy;

public class PatrolListener implements StepListener {
    private Enemy enemy;

    public PatrolListener(Enemy enemy){ 
        this.enemy = enemy;
    }

    @Override
    public void preStep(StepEvent e) {
        enemy.patrol();
    }

    @Override
    public void postStep(StepEvent e) {
    }

}
