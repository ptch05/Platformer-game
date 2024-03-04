package main;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.*;
import entities.Player;
import entities.Skeleton;
import input.InputHandler;
import objects.Armour;
import objects.Potion;
import objects.Spikes;
import objects.Trophy;
import utilities.PlayerCollisions;
import utilities.SkeletonPatrolListener;

public class GameWorld extends World {
    // Constructor and methods for world management
    private Player player;
    private List<Skeleton> skeleton = new ArrayList<>();
    private List<SkeletonPatrolListener> skeletonPatrolListener = new ArrayList<>();

    private InputHandler inputHandler;
    private int []skeletonPatrolLeftBoundary = {25, 45,170,240,375}; 
    private int []skeletonPatrolRightBoundary = {35,70,180,250,385};
    private static float XPos = -68.15f;
    private static float YPos = -13f;
    private static Vec2[] skeletonPositions = {
        new Vec2(30, -7.3f),
        new Vec2(60, -7.3f),
        new Vec2(175, -3),
        new Vec2(245, 15),
        new Vec2(380, 20)
    };
    private static final int numberOfSkeletons = 5;
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
        XPos = -68.15f; // Reset X position for ground creation
        YPos = -13f;
        createEnvironment();
        player = new Player(this);
        player.setPosition(new Vec2(5, -5));
        inputHandler = new InputHandler(this);
        player.setInputHandler(inputHandler);
        player.createPlayerFixtureWithFriction();

        addSkeletons();

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
        ground.addImage(new BodyImage("assets/images/level-data/ground.png", 11.5f));
        XPos +=7f;
    }

    private void createSpikes(){
        Spikes spikes = new Spikes(this);
        spikes.setPosition(new Vec2(XPos, YPos));
        XPos+=3f;
      }

    private void createEnvironment(){

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

        

        YPos = -12f;
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

        YPos=5;
        for(int i =0; i<14;i++){
            createSpikes();
        }

        YPos=15.3f;
        XPos-=25;
        createGround();

        XPos+=20f;
        YPos= 10;
        for(int i=0; i<15;i++){
            createGround();
        }

        for(int i=0; i<18; i++){
            createSpikes();
        }
    }

    private void addSkeletons() {
        for (int i = 0; i < numberOfSkeletons; i++) {
            Skeleton newSkeleton = new Skeleton(this, skeletonPatrolLeftBoundary[i], skeletonPatrolRightBoundary[i]);
            newSkeleton.setPosition(skeletonPositions[i]); // Set initial position
            skeleton.add(newSkeleton);

            SkeletonPatrolListener listener = new SkeletonPatrolListener(newSkeleton);
            skeletonPatrolListener.add(listener);
            this.addStepListener(listener); // Add listener to the world for each skeleton
        }
    }

    
    
}