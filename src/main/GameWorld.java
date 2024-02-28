package main;
import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import entities.Player;
import entities.Skeleton;
import input.InputHandler;
import utilities.PlayerCollisions;
import utilities.SkeletonPatrolListener;

public class GameWorld extends World {
    // Constructor and methods for world management
    private Player player;
    private Skeleton skeleton;
    private InputHandler inputHandler;
    private float skeletonPatrolLeftBoundary = -10f; 
    private float skeletonPatrolRightBoundary = 10f;

    public GameWorld() {
        // Initialize world here
        Shape groundShape = new BoxShape(50, 2.5f); 
        StaticBody ground = new StaticBody(this, groundShape);
        ground.setPosition(new Vec2(-14.15f, -13f));

        float imageWidth = 5.0f; 
        int numTiles = (int) Math.ceil(100 / imageWidth);

        for (int i = 0; i < numTiles; i++) {
            // Calculate the x position for each image
            float xPos = -50 + (i * imageWidth) + (imageWidth / 2); // Start tiling from one end of the ground
            // Add the image at this x position with the same y position as the ground
            ground.addImage(new BodyImage("assets/images/level-data/ground.png", 5f)).setOffset(new Vec2(xPos, 0));
        }

        

        player = new Player(this, inputHandler); 
        inputHandler = new InputHandler(player); 
        player.setPosition(new Vec2(-12, -13));

        skeleton = new Skeleton(this, skeletonPatrolLeftBoundary, skeletonPatrolRightBoundary);
        skeleton.setPosition(new Vec2(0,(float)-7.3));

        // Add a StepListener to handle the Skeleton patrolling
        SkeletonPatrolListener skeletonPatrolListener = new SkeletonPatrolListener(skeleton);
        this.addStepListener(skeletonPatrolListener);

        PlayerCollisions collision = new PlayerCollisions(player);
        player.addCollisionListener(collision);
    }

    public Player getPlayer(){
        return player;
    }
}
