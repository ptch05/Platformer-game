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


    protected void restartGame() {
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


    protected void createGround(){
        Shape groundShape = new BoxShape((float) 4.7, 5.4f);
        StaticBody ground = new StaticBody(this, groundShape);
        ground.setPosition(new Vec2(XPos, YPos));
        ground.addImage(new BodyImage("./assets/images/level-data/level1/ground.png", 11.5f));
        XPos +=7f;
    }

    protected void createSpikes(){
        Spikes spikes = new Spikes(this);
        spikes.setPosition(new Vec2(XPos, YPos));
        XPos+=3f;
    }

    
    public void addGround(int times) {
        //Adds in ground every time it's called
        for (int i = 0; i < times; i++) {
            createGround();
        }
    }

    public void addSpikes(int times) {
        //Adds in spikes every time it's called
        for (int i = 0; i < times; i++) {
            createSpikes();
        }
    }

}