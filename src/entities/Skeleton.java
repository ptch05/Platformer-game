package entities;

import city.cs.engine.*;
import main.GameWorld;

public class Skeleton extends Walker{

  private static final Shape skeletonShape = new BoxShape(1, 2);
    private static final BodyImage skeletonImage = new BodyImage("assets/images/skeleton/skeleton-clothed.gif", 5);

    public Skeleton(GameWorld world) {
        super(world, skeletonShape);
        addImage(skeletonImage);
    }
   
}
