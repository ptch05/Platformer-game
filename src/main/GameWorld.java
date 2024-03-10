package main;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.Enemy;
//import entities.Ghost;
import entities.Hound;
import entities.Player;
import entities.Skeleton;
import input.InputHandler;
import objects.*;
import utilities.PlayerCollisions;
import utilities.PatrolListener;

public class GameWorld extends World {
    // Constructor and methods for world management
    private Player player;
    private List<Enemy> enemies = new ArrayList<>();
    private List<PatrolListener> patrolListeners = new ArrayList<>();

    private InputHandler inputHandler;
    private static final int numberOfSkeletons = 7;
    private static final int numberOfHounds = 2;
   // private static final int numberOfGhosts = 3;
    private static final int numberOfPotions = 2;
    private int []skeletonPatrolLeftBoundary = {25, 52, 91, 160,235,315, 380}; 
    private int []skeletonPatrolRightBoundary = {35,75, 100, 175,245,335, 400};
    private int []houndPatrolLeftBoundary = {0,350}; 
    private int []houndPatrolRightBoundary = {20,370};
    /*private int []ghostPatrolUpperBoundary = {0,350}; 
     private int []ghostPatrolLowerBoundary = {20,370};*/
    private static float XPos = -68.15f;
    private static float YPos = -13f;
    private static Vec2[] skeletonPositions = {
        new Vec2(30, -7.3f),
        new Vec2(60, -7.3f),
        new Vec2(95, 1f),
        new Vec2(170, -3),
        new Vec2(240, 15),
        new Vec2(325, 22),
        new Vec2(390, 22)
    };
    private static Vec2[] houndPositions = {
        new Vec2(10, -5.8f),
        new Vec2(352, 20)
    };

    /*private static Vec2[] ghostPositions = {
        new Vec2(10, -5.8f),
        new Vec2(352, 20)
    };*/


    private static float[] potionXPos = {60, 175};
    private static float[] potionYPos = {-6, -1};

    public GameWorld() {
        initializeWorld();
    }

    public Player getPlayer(){
        return player;
    }

    public void restartGame() {
        this.stop();
        clearBodies();
        initializeWorld();
        inputHandler.setPlayer(player);
        this.start();
    }
    

    public void clearBodies() {
        //Deletes all bodies in the world at once
        List<DynamicBody> dynamicBodies = this.getDynamicBodies();
        for (DynamicBody body : dynamicBodies) {
            body.destroy();
        }
        List<StaticBody> staticBodies = this.getStaticBodies();
        for (StaticBody body : staticBodies) {
            body.destroy();
        }
    }

    public void initializeWorld() { 
        //Uses this method to make the world every time
        XPos = -68.15f; // Resets X position for ground creation
        YPos = -13f; // Resets Y position for ground creation
        createEnvironment();
        player = new Player(this);
        player.setPosition(new Vec2(-3, -5));
        inputHandler = new InputHandler(this);
        player.setInputHandler(inputHandler);
        player.addFriction();

        addSkeletons();
        addHounds();

        PlayerCollisions collision = new PlayerCollisions(player, this);
        player.addCollisionListener(collision);

        for(int i=0; i<numberOfPotions; i++){
            Potion potion = new Potion(this);
            potion.setPosition(new Vec2(potionXPos[i], potionYPos[i]));
        }
        
        Armour armour = new Armour(this);
        armour.setPosition(new Vec2(230, 17));
        AudioHandler.playSpawnSound();
        AudioHandler.playGameMusic();

        Trophy trophy = new Trophy(this);
        trophy.setPosition(new Vec2(410,27));
    }

    private void createGround(){
        Shape groundShape = new BoxShape((float) 4.7, 5.4f);
        StaticBody ground = new StaticBody(this, groundShape);
        ground.setPosition(new Vec2(XPos, YPos));
        ground.addImage(new BodyImage("./assets/images/level-data/ground.png", 11.5f));
        XPos +=7f;
    }

    private void createSpikes(){
        Spikes spikes = new Spikes(this);
        spikes.setPosition(new Vec2(XPos, YPos));
        XPos+=3f;
      }

      private void createEnvironment() {
        // Logic for the first level
        addSpikes(18);
        addGround(14);

        YPos = -8f;
        addGround(4);

        YPos = -9f;
        addSpikes(16);
    
        XPos -= 20f;
        YPos = -8f;
        addGround(1);
        XPos += 16f;
        addGround(4);
        
        for(int i=0; i<4; i++){
            addGround(1);
            YPos +=4.5f;
        }
        addGround(7);
        YPos=9;
        addSpikes(14);
    
        YPos = 15.3f;
        XPos -= 25;
        addGround(1);
        XPos += 20f;
        YPos= 11;
        addGround(15);
        YPos =10;
        addSpikes(18);
    }
    
    private void addGround(int times) {
        //Adds in ground every time it's called
        for (int i = 0; i < times; i++) {
            createGround();
        }
    }
 
    private void addSpikes(int times) {
        //Adds in spikes every time it's called
        for (int i = 0; i < times; i++) {
            createSpikes();
        }
    }

    private void addSkeletons() {
        //Adds the enemies into the world 
        for (int i = 0; i < numberOfSkeletons; i++) {
            Skeleton newSkeleton = new Skeleton(this, skeletonPatrolLeftBoundary[i], skeletonPatrolRightBoundary[i]);
            createEnemy(newSkeleton, skeletonPositions[i]);
        }
    }

    private void addHounds(){
        for (int i = 0; i < numberOfHounds; i++) {
            Hound newHound = new Hound(this, houndPatrolLeftBoundary[i], houndPatrolRightBoundary[i]);
            createEnemy(newHound, houndPositions[i]);
        }
    }

   /*  private void addGhosts(){
        for (int i = 0; i < numberOfGhosts; i++) {
            Ghost newGhost = new Ghost(this, ghostPatrolUpperBoundary[i], ghostPatrolLowerBoundary[i]);
            createEnemy(newGhost, ghostPositions[i]);
        }
    }*/

    private void createEnemy(Enemy enemy, Vec2 position) {
        enemy.setPosition(position); // Sets initial position of the enemies
        enemies.add(enemy);

        PatrolListener listener = new PatrolListener(enemy);
        patrolListeners.add(listener);
        this.addStepListener(listener); // Then it adds the listeners for each of the enemies to the world
    }

}