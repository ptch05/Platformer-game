package entities;

import city.cs.engine.*;
import main.GameWorld;


public class Player extends Walker {
    
    private static final Shape characterShape = new BoxShape(1, 2);
    private static final BodyImage IDLE_RIGHT = new BodyImage("assets/images/hero/hero-idle-right.gif", 5);
    

    public Player(GameWorld world) {
        super(world, characterShape);
        addImage(IDLE_RIGHT);
    }
   
}