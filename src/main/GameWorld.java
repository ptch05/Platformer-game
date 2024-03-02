package main;
import java.util.List;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import entities.Player;
import entities.Skeleton;
import input.InputHandler;
import interactables.Armour;
import interactables.Potion;
import interactables.Spikes;
import interactables.Trophy;
import utilities.AudioHandler;
import utilities.PlayerCollisions;
import utilities.SkeletonPatrolListener;

public class GameWorld extends World {
    // Constructor and methods for world management
    private Player player;
    private Skeleton skeleton;
    private InputHandler inputHandler;
    private float skeletonPatrolLeftBoundary = -10f; 
    private float skeletonPatrolRightBoundary = 10f;
    private static float XPos = -14.15f;
    private static float YPos = -13f;

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

    private void initializeWorld() { 
        createEnvironment();
        player = new Player(this, inputHandler); 
        player.setPosition(new Vec2(-12, -5));
        
        inputHandler = new InputHandler(player); 

        skeleton = new Skeleton(this, skeletonPatrolLeftBoundary, skeletonPatrolRightBoundary);
        skeleton.setPosition(new Vec2(0,(float)-7.3));

        // Added a StepListener to handle the Skeleton patrolling
        SkeletonPatrolListener skeletonPatrolListener = new SkeletonPatrolListener(skeleton);
        this.addStepListener(skeletonPatrolListener);

        PlayerCollisions collision = new PlayerCollisions(player);
        player.addCollisionListener(collision);

        Potion potion = new Potion(this);
        potion.setPosition(new Vec2(3, -6));

        
        Armour armour = new Armour(this);
        armour.setPosition(new Vec2(10, -6));
        AudioHandler.playGameMusic();

        Trophy trophy = new Trophy(this);
        trophy.setPosition(new Vec2(410,27));
    }

    private void createEnvironment(){
        for(int i =0; i<14; i++){
            createGround();
        }
        YPos = -8f;
        
        for(int i=0; i<4; i++){
            createGround();
            System.out.println("XPos" +XPos);
        }

        

        YPos = -12f;
        for(int i=0; i<16; i++){
            createSpikes();
            System.out.println("XPos"+XPos);
        }

        XPos -=20f;
        YPos = -8f;
        for(int i=0; i<1;i++){
            createGround();
            System.out.println("XPos" +XPos);
        }

       XPos+=16f;
        for(int i=0; i<4; i++){
            createGround();
            System.out.println("XPos" +XPos);
        }

        for(int i=0;i<2;i++){
            createGround();
            YPos+=4.5f;
        }

        
        for(int i=0;i<2;i++){
            createGround();
            YPos+=4.5f;
        }

        for(int i=0; i<7;i++){
            createGround();
            System.out.println(YPos);
        }

        YPos=5;
        for(int i =0; i<14;i++){
            createSpikes();
            System.out.println(XPos);
        }

        YPos=15.3f;
        XPos-=25;
        createGround();

        XPos+=20f;
        YPos= 10;
        for(int i=0; i<15;i++){
            createGround();
            System.out.println(XPos);
        }


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

    public void addSkeletons() {
        int numberOfSkeletons = 5;
    }

    public void addPotions(){
    }
    
    
    
}