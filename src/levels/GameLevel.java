package levels;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import collectibles.Armour;
import collectibles.Trophy;
import entities.*;
import input.InputHandler;
import main.Game;
import objects.*;
import utilities.PatrolListener;
import utilities.PlayerCollisions;

/**
 * An abstract class representing a level in the game. It includes the setup
 * for the player, enemies, collectibles, and the environment. Each level has
 * specific enemies, obstacles, and goals that need to be completed to progress.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public abstract class GameLevel extends World {
  protected Player player;
  protected Skeleton skeleton;
  protected Hound hound;
  protected Ghost ghost;
  protected Game game;
  protected InputHandler inputHandler;
  protected Armour armour;
  protected Trophy trophy;
  protected Demon demon;
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

  /**
   * Constructs a game level with a reference to the main game object. It initializes
   * the level by adding world components like the ground, enemies, and the player.
   * 
   * @param game The game controller that manages the levels and transitions.
   */
  public GameLevel(Game game){
    this.game = game;
    this.addWorldComponents();
  }

  public Player getPlayer() {
    return player;
  }

    /**
     * Adds skeletons to the level based on the number and positions predefined for the level.
     */
    protected void addSkeletons() {
        //Adds the enemies into the world 
        for (int i = 0; i < numberOfSkeletons; i++) {
            Skeleton newSkeleton = new Skeleton(this, skeletonPatrolLeftBoundary[i], skeletonPatrolRightBoundary[i]);
            createEnemy(newSkeleton, skeletonPositions[i]);
        }
    }

    /**
     * Adds hounds to the level based on the number and positions predefined for the level.
     */
    protected void addHounds(){
        for (int i = 0; i < numberOfHounds; i++) {
            Hound newHound = new Hound(this, houndPatrolLeftBoundary[i], houndPatrolRightBoundary[i]);
            createEnemy(newHound, houndPositions[i]);
        }
    }

    /**
     * Adds ghosts to the level based on the number and positions predefined for the level.
     */
    protected void addGhosts(){
        for (int i = 0; i < numberOfGhosts; i++) {
            Ghost newGhost = new Ghost(this, player);
            createEnemy(newGhost, ghostPositions[i]);
        }
    }

    /**
     * Creates an enemy in the world at a specific position and registers a patrol listener
     * if necessary. This method centralizes the creation and setup of enemies.
     * 
     * @param enemy    The enemy to be added to the world.
     * @param position The position at which to place the enemy.
     */
    protected void createEnemy(Enemy enemy, Vec2 position) {
        enemy.setPosition(position); // Sets initial position of the enemies
        enemies.add(enemy);

        PatrolListener listener = new PatrolListener(enemy);
        patrolListeners.add(listener);
        this.addStepListener(listener); // Then it adds the listeners for each of the enemies to the world
    }

    /**
     * Restarts the game level by resetting the player and all level components to their
     * initial state.
     */
    public void restartGame() {
        this.stop(); 
        for (Enemy enemy : enemies) {
            if (enemy instanceof Ghost) {
                ((Ghost)enemy).removeListener(); // Remove all ghost listeners
            }
        }
        clearBodies(); 
        addWorldComponents(); 
        inputHandler.setPlayer(player); 
        initializeWorld(); 
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
        float groundWidth, groundHeight, imageHeight;
        String imagePath;
    
        switch (levelName) {
            case "Level1":
                groundHeight = 5.4f;
                imagePath = "./assets/images/level-data/level1/ground.png";
                imageHeight = 11.5f;
                groundWidth = 4.7f;
                break;
            case "Level2":
                groundHeight = 4.1f;
                imagePath = "./assets/images/level-data/level2/ground.png";
                imageHeight = 9f;
                groundWidth = 4.7f;
                break;
            case "Level3":
                groundHeight = 2.8f;
                imagePath = "./assets/images/level-data/level3/ground.png";
                imageHeight = 6.6f;
                groundWidth = 5.7f;
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
        if(getLevelName() == "Level1"){
            spikes.setPosition(new Vec2(XPos, YPos+1f));
            XPos+=3f;
        } else if(getLevelName() == "Level2"){
            spikes.setPosition(new Vec2(XPos, YPos));
            XPos+=3.5f;
        }
    }

    
    protected void addGround(int times) {
        //Adds in ground every time it's called
        for (int i = 0; i < times; i++) {
            createGround(getLevelName());
        }
    }

    protected void addSpikes(int times) {
        //Adds in spikes every time it's called
        for (int i = 0; i < times; i++) {
            createSpikes();
        }
    }

    /**
     * Gets the name of the current level. This method must be implemented by all subclasses
     * to identify the level.
     * 
     * @return The name of the level.
     */
    public abstract String getLevelName();

    /**
     * Determines if the level has been completed based on the level's specific completion
     * criteria.
     * 
     * @return true if the level is complete, false otherwise.
     */
    public abstract boolean isComplete();

    protected void addWorldComponents() {
        player = new Player(this);
        player.setGameLevel(this);

        if(getLevelName() == "Level1"){
            player.setPosition(new Vec2(-3, -5));
        } else if(getLevelName() == "Level2"){
            player.setPosition(new Vec2(200,-6));
        } else if(getLevelName() == "Level3"){
            player.setPosition(new Vec2(135,-5));
        }
        inputHandler = new InputHandler(player, this);
        player.addFriction();

        PlayerCollisions collision = new PlayerCollisions(player, this, game);
        player.addCollisionListener(collision);
    }

    /**
     * Initializes and adds world components to the level. This includes setting up the player,
     * enemies, collectibles, and environmental objects specific to the level.
     */
    protected abstract void initializeWorld();

}