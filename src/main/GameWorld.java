package main;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.Enemy;
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
    private int []skeletonPatrolLeftBoundary = {25, 45,160,225,330}; 
    private int []skeletonPatrolRightBoundary = {35,70,175,260,350};
    private int []houndPatrolLeftBoundary = {0,390}; 
    private int []houndPatrolRightBoundary = {20,355};
    private static float XPos = -68.15f;
    private static float YPos = -13f;
    private static Vec2[] skeletonPositions = {
        new Vec2(30, -7.3f),
        new Vec2(60, -7.3f),
        new Vec2(170, -3),
        new Vec2(240, 15),
        new Vec2(365, 22)
    };
    private static Vec2[] houndPositions = {
        new Vec2(10, -7.3f),
        new Vec2(375, 18)
    };
    private static final int numberOfSkeletons = 5;
    private static final int numberOfHounds = 2;
    private static final int numberOfPotions = 2;
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
    

    private void clearBodies() {
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
        XPos = -68.15f; // Resets X position for ground creation
        YPos = -13f; // Resets Y position for ground creation
        createEnvironment();
        player = new Player(this);
        player.setPosition(new Vec2(5, -5));
        inputHandler = new InputHandler(this);
        player.setInputHandler(inputHandler);
        player.createPlayerFixtureWithFriction();

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

    private void createEnvironment(){
        //Logic for the first level

        for(int i=0; i<18; i++){
            createSpikes();
        }

        for(int i =0; i<14; i++){
            createGround();
        }
        YPos = -8f;
        
        for(int i=0; i<4; i++){
            createGround();
        }

        YPos = -9f;
        for(int i=0; i<16; i++){
            createSpikes();
        }

        XPos -=20f;
        YPos = -8f;
        for(int i=0; i<1;i++){
            createGround();
        }

       XPos+=16f;
        for(int i=0; i<4; i++){
            createGround();
        }

        for(int i=0;i<4;i++){
            createGround();
            YPos+=4.5f;
        }

        for(int i=0; i<7;i++){
            createGround();
        }

        YPos=9;
        for(int i =0; i<14;i++){
            createSpikes();
        }

        YPos=15.3f;
        XPos-=25;
        createGround();

        XPos+=20f;
        YPos= 11;
        for(int i=0; i<15;i++){
            createGround();
        }

        YPos =10;
        for(int i=0; i<18; i++){
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

    private void createEnemy(Enemy enemy, Vec2 position) {
        enemy.setPosition(position); // Sets initial position of the enemies
        enemies.add(enemy);

        PatrolListener listener = new PatrolListener(enemy);
        patrolListeners.add(listener);
        this.addStepListener(listener); // Then it adds the listeners for each of the enemies to the world
    }

}