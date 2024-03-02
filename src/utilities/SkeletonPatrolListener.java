package utilities;

import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import entities.Skeleton;

public class SkeletonPatrolListener implements StepListener {
    private Skeleton skeleton;

    public SkeletonPatrolListener(Skeleton skeleton) { 
        this.skeleton = skeleton;
    
    }

    @Override
    public void preStep(StepEvent e) {
        skeleton.patrol();
    }

    @Override
    public void postStep(StepEvent e) {
    }

}
