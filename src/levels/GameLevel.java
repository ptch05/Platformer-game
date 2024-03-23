package levels;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import entities.*;
import input.InputHandler;
import main.Game;
import objects.*;
import utilities.PatrolListener;
import utilities.PlayerCollisions;

public abstract class GameLevel extends World {
  protected Player player;
  protected Skeleton skeleton;
  protected Hound hound;
  protected Ghost ghost;
  protected Game game;
  protected InputHandler inputHandler;
  protected Armour armour;
  protected Trophy trophy;
  protected List<Enemy> enemies = new ArrayList<>();
  protected List<PatrolListener> patrolListeners = new ArrayList<>();
  protected int numberOfSkeletons;
  protected int numberOfHounds;
  protected int numberOfGhosts;
  protected int numberOfPotions;
  protected int []skeletonPatrolLeftBoundary;
  protected int []skeletonPatrolRightBoundary;
  protected int []houndPatrolLeftBoundary;
  protected int []houndPatrolRightBoundary;
  protected float XPos;
  protected float YPos;
  protected Vec2[] skeletonPositions;
  protected Vec2[] houndPositions;
  protected Vec2[] ghostPositions;
  protected float[] potionXPos;
  protected float[] potionYPos;
  protected boolean levelCleared;

  public GameLevel(Game game){
    this.game = game;

    player = new Player(this);
    inputHandler = new InputHandler(player, this);
    player.addFriction();

    PlayerCollisions collision = new PlayerCollisions(player, this, game);
    player.addCollisionListener(collision);
  }

  public Player getPlayer() {
    return player;
  }


    protected void addSkeletons() {
        //Adds the enemies into the world 
        for (int i = 0; i < numberOfSkeletons; i++) {
            Skeleton newSkeleton = new Skeleton(this, skeletonPatrolLeftBoundary[i], skeletonPatrolRightBoundary[i]);
            createEnemy(newSkeleton, skeletonPositions[i]);
        }
    }

    protected void addHounds(){
        for (int i = 0; i < numberOfHounds; i++) {
            Hound newHound = new Hound(this, houndPatrolLeftBoundary[i], houndPatrolRightBoundary[i]);
            createEnemy(newHound, houndPositions[i]);
        }
    }

    protected void addGhosts(){
        for (int i = 0; i < numberOfGhosts; i++) {
            Ghost newGhost = new Ghost(this, player);
            createEnemy(newGhost, ghostPositions[i]);
        }
    }

    protected void createEnemy(Enemy enemy, Vec2 position) {
        enemy.setPosition(position); // Sets initial position of the enemies
        enemies.add(enemy);

        PatrolListener listener = new PatrolListener(enemy);
        patrolListeners.add(listener);
        this.addStepListener(listener); // Then it adds the listeners for each of the enemies to the world
    }


    public void restartGame() {
        this.stop();
        clearBodies();
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


    protected void createGround(String levelName) {
        Shape groundShape;
        BodyImage groundImage;
        float groundWidth = 4.7f, groundHeight, imageHeight;
        String imagePath;
    
        switch (levelName) {
            case "Level1":
                groundHeight = 5.4f;
                imagePath = "./assets/images/level-data/level1/ground.png";
                imageHeight = 11.5f;
                break;
            case "Level2":
                groundHeight = 4.1f;
                imagePath = "./assets/images/level-data/level2/ground.png";
                imageHeight = 9f;
                break;
            case "Level3":
                groundHeight = 3.5f;
                imagePath = "./assets/images/level-data/level3/ground.png";
                imageHeight = 11.5f;
                break;
            default:
                throw new IllegalArgumentException("Invalid level name: " + levelName);
        }
    
        groundShape = new BoxShape(groundWidth, groundHeight);
        StaticBody ground = new StaticBody(this, groundShape);
        ground.setPosition(new Vec2(XPos, YPos));
        groundImage = new BodyImage(imagePath, imageHeight);
        ground.addImage(groundImage);
        XPos += 7f;
    }

    protected void createSpikes(){
        Spikes spikes = new Spikes(this, this);
        spikes.setPosition(new Vec2(XPos, YPos+1f));
        if(getLevelName() == "Level1"){
            XPos+=3f;
        } else if(getLevelName() == "Level2"){
            XPos+=3.5f;
        }
    }

    
    public void addGround(int times) {
        //Adds in ground every time it's called
        for (int i = 0; i < times; i++) {
            createGround(getLevelName());
        }
    }

    public void addSpikes(int times) {
        //Adds in spikes every time it's called
        for (int i = 0; i < times; i++) {
            createSpikes();
        }
    }

    public abstract String getLevelName();

    public abstract boolean isComplete();

}