package main;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.FixtureDef;

import city.cs.engine.*;
import entities.Player;
import entities.Skeleton;

public class GameWorld extends World {
    // Constructor and methods for world management
    private Player player;
    private Skeleton skeleton;

    public GameWorld() {
        // Initialize your world here
        Shape groundShape = new BoxShape(50, 2.5f); // The ground is 100 meters wide in total
        StaticBody ground = new StaticBody(this, groundShape);
        ground.setPosition(new Vec2(-14.15f, -13f));

        // Assuming the image is 5 meters wide
        float imageWidth = 5.0f; 
        // Calculate how many images you need to cover the ground width
        int numTiles = (int) Math.ceil(100 / imageWidth); // 100 is the total width of the ground

        for (int i = 0; i < numTiles; i++) {
            // Calculate the x position for each image
            float xPos = -50 + (i * imageWidth) + (imageWidth / 2); // Start tiling from one end of the ground
            // Add the image at this x position with the same y position as the ground
            ground.addImage(new BodyImage("assets/images/level-data/ground.png", 5f)).setOffset(new Vec2(xPos, 0));
        }
       

        player = new Player(this);
        player.setPosition(new Vec2(-12,(float) -13));
        skeleton = new Skeleton(this);
        skeleton.setPosition(new Vec2(0,-12));
    }

    public Player getPlayer(){
        return player;
    }
}
