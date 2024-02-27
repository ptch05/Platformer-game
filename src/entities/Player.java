package entities;
import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import main.GameWorld;
import input.InputHandler;


public class Player extends Walker {
    private static float xNum = 3;
    private static float yNum = (float)3.5;
    private static int playerSize = 7;
    public static final float WALKING_SPEED = 20;
    private static final float GRAVITY_FORCE = -25f;
    private boolean facingRight = true; 
    private boolean inAir = false;
    private boolean isAttacking = false;
    private long attackStartTime;
    private long attackDuration = 1000;

    private static final Shape characterShape = new BoxShape(xNum, yNum);

    private static final BodyImage IDLE_RIGHT = new BodyImage("assets/images/hero/hero-idle-right.gif", playerSize);
    private static final BodyImage RUN_RIGHT = new BodyImage("assets/images/hero/hero-run-right.gif", playerSize);
    private static final BodyImage JUMP_RIGHT = new BodyImage("assets/images/hero/hero-jump-right.gif", playerSize);
    private static final BodyImage ATTACK_RIGHT = new BodyImage("assets/images/hero/hero-attack-right.gif", playerSize);
    private static final BodyImage HURT_RIGHT = new BodyImage("assets/images/hero/hero-hurt-right.png", playerSize);
    private static final BodyImage IDLE_LEFT = new BodyImage("assets/images/hero/hero-idle-left.gif", playerSize);
    private static final BodyImage RUN_LEFT = new BodyImage("assets/images/hero/hero-run-left.gif", playerSize);
    private static final BodyImage JUMP_LEFT = new BodyImage("assets/images/hero/hero-jump-left.gif", playerSize);
    private static final BodyImage ATTACK_LEFT = new BodyImage("assets/images/hero/hero-attack-left.gif", playerSize);
    private static final BodyImage HURT_LEFT = new BodyImage("assets/images/hero/hero-hurt-right.png", playerSize);
    

    private GameWorld world;
    private BodyImage currentImage;
    private InputHandler inputHandler;

    public Player(GameWorld world, InputHandler inputHandler) {
        super(world, characterShape);
        this.world = world;
        currentImage = IDLE_RIGHT;
        addImage(currentImage);
        createPlayerFixtureWithFriction();
        this.inputHandler = inputHandler;
    }

    public void runRight() {
        removeAllImages();
        currentImage = RUN_RIGHT;
        addImage(currentImage);
        facingRight = true;
    }
    public void runLeft() {
        removeAllImages();
        currentImage = RUN_LEFT;
        addImage(currentImage);
        facingRight = false;
    }

    

    public void jumpRight() {
        removeAllImages();
        currentImage = JUMP_RIGHT;
        addImage(currentImage);
        inAir = true;
    }
    public void jumpLeft() {
        removeAllImages();
        currentImage = JUMP_LEFT;
        addImage(currentImage);
        facingRight = false;
        inAir = true;
    }


    public void idleRight() {
        removeAllImages();
        addImage(IDLE_RIGHT);
    }

    public void idleLeft() {
        removeAllImages();
        addImage(IDLE_LEFT);
        facingRight = false;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
    
    public boolean isFacingRight() {
        return facingRight;
    }

    public boolean isInAir() {
        return inAir;
    }
    
    public void setInAir(boolean inAir) {
        this.inAir = inAir;
    }
    
    public void update() {
        // Simulate gravity by applying a force when the player is in the air.
        if (isInAir()) {
            this.applyForce(new Vec2(0, GRAVITY_FORCE));
        }
    
        // Handling the end of an attack animation
        if (isAttacking && System.currentTimeMillis() - attackStartTime > attackDuration) {
            isAttacking = false;
            
            // Check if inputHandler is not null before using it
            if (inputHandler != null) {
                if (!inputHandler.isKeyAPressed() && !inputHandler.isKeyDPressed()) {
                    if (facingRight) {
                        idleRight();
                    } else {
                        idleLeft();
                    }
                } else {
                    if (inputHandler.isKeyAPressed()) {
                        runLeft();
                    } 
                    if (inputHandler.isKeyDPressed()) {
                        runRight();
                    }
                }
        
                // After attack, if inAir is still true, should go back to jump animation
                if (inAir) {
                    if (facingRight) {
                        jumpRight();
                    } else {
                        jumpLeft();
                    }
                }
            }
        }
    }
    

public void attack() {
    if (!isAttacking) {
        removeAllImages();
        isAttacking = true;
        attackStartTime = System.currentTimeMillis();
        if (facingRight) {
            currentImage = ATTACK_RIGHT;
        } else {
            currentImage = ATTACK_LEFT;
        }
        addImage(currentImage);
    }
}
    
public void adjustGravity(float newGravityScale) {
    this.setGravityScale(newGravityScale);
}

public void createPlayerFixtureWithFriction() {
    SolidFixture fixture = new SolidFixture(this, characterShape);
    fixture.setFriction(270f); 
}

public void endAttack() {
    if (isAttacking = true){
        isAttacking = false;
        }
    }

}



