package entities;
import org.jbox2d.common.Vec2;

import city.cs.engine.*;
import main.GameWorld;
import utilities.AudioHandler;
import input.InputHandler;


public class Player extends Walker {
    private static int xNum = 3;
    private static float yNum = (float)3.8;
    private static int playerSize = 7;
    public static final float WALKING_SPEED = 20;
    private static final float GRAVITY_FORCE = -25f;
    private boolean facingRight = true; 
    private boolean inAir = false;
    private boolean isAttacking = false;
    private Fixture attackFixture;
    private long attackStartTime;
    private long attackDuration = 1000;
    private int health;
    private int damageAmount = 40;
    private long lastDamageTime;
    private static final long DAMAGE_COOLDOWN = 1000;

    private static final Shape characterShape = new BoxShape(xNum, yNum);
    private static final Shape attackShape = new BoxShape((float) 5.75, yNum);
    private static final BodyImage IDLE_RIGHT = new BodyImage("assets/images/hero/hero-idle-right.gif", playerSize);
    private static final BodyImage RUN_RIGHT = new BodyImage("assets/images/hero/hero-run-right.gif", playerSize);
    private static final BodyImage JUMP_RIGHT = new BodyImage("assets/images/hero/hero-jump-right.gif", playerSize);
    private static final BodyImage ATTACK_RIGHT = new BodyImage("assets/images/hero/hero-attack-right.gif", playerSize);
    private static final BodyImage HURT_RIGHT = new BodyImage("assets/images/hero/hero-hurt-right.png",(float) (playerSize*1.3));
    private static final BodyImage IDLE_LEFT = new BodyImage("assets/images/hero/hero-idle-left.gif", playerSize);
    private static final BodyImage RUN_LEFT = new BodyImage("assets/images/hero/hero-run-left.gif", playerSize);
    private static final BodyImage JUMP_LEFT = new BodyImage("assets/images/hero/hero-jump-left.gif", playerSize);
    private static final BodyImage ATTACK_LEFT = new BodyImage("assets/images/hero/hero-attack-left.gif", playerSize);
    private static final BodyImage HURT_LEFT = new BodyImage("assets/images/hero/hero-hurt-right.png",(float) (playerSize*1.3));
    

    private GameWorld world;
    private BodyImage currentImage;
    private InputHandler inputHandler;

    public Player(GameWorld world, InputHandler inputHandler) {
        super(world, characterShape);
        this.world = world;
        addImage(IDLE_RIGHT);
        createPlayerFixtureWithFriction();
        this.inputHandler = inputHandler;
        this.health = 100;
    }

    public void runRight() {
        removeAllImages();
        addImage(RUN_RIGHT);
        facingRight = true;
    }
    public void runLeft() {
        removeAllImages();
        addImage(RUN_LEFT);
        facingRight = false;
    }

    public void jumpRight() {
            removeAllImages();
            addImage(JUMP_RIGHT);
            inAir = true;
            facingRight = true;
            AudioHandler.playJumpSound();
    }

    public void jumpLeft() {
            removeAllImages();
            addImage(JUMP_LEFT);
            facingRight = false;
            inAir = true;
           AudioHandler.playJumpSound();
    }

    public void idleRight() {
        removeAllImages();
        addImage(IDLE_RIGHT);
        facingRight = true;
    }

    public void idleLeft() {
        removeAllImages();
        addImage(IDLE_LEFT);
        facingRight = false;
    }

    public void hurtLeft(){
        removeAllImages();
        addImage(HURT_LEFT);
    }

    public void hurtRight(){
        removeAllImages();
        addImage(HURT_RIGHT);
    }
   
    public boolean isFacingRight() {
        return facingRight;
    }

    public boolean isInAir() {
        return inAir;
    }
    
    public void update() {
        // Simulate gravity by applying a force when the player is in the air.
        if (isInAir()) {
            this.applyForce(new Vec2(0, GRAVITY_FORCE));
        }else{ }

        if (this.health <= 0) {
            handleDeath();
        }
    
        // Handling the end of an attack animation
        if (isAttacking() && System.currentTimeMillis() - attackStartTime > attackDuration) {
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
        if (!isAttacking()) {
            isAttacking = true;
            attackStartTime = System.currentTimeMillis();
            removeAllImages();
            if (facingRight) {
                currentImage = ATTACK_RIGHT;
                createAttackHitbox(attackShape);
            } else {
                currentImage = ATTACK_LEFT;
                createAttackHitbox(attackShape);
            }
            addImage(currentImage);
            AudioHandler.playAttackSound();
        }
    }

    private void createAttackHitbox(Shape attackShape) {
        if (attackFixture != null) {
            attackFixture.destroy(); // Use destroy() method directly on the fixture
            attackFixture = null; // Clear the reference after destruction
        }
        attackFixture = new SolidFixture(this, attackShape); // Create a new attack hitbox
        
    }
    
    public void endAttack() {
        if (isAttacking) {
            isAttacking = false;
            if (attackFixture != null) {
                attackFixture.destroy(); // Remove the attack hitbox using destroy()
                attackFixture = null; // Clear the reference after destruction
            }
        }
    }
    

    public boolean isAttacking() {
        return isAttacking;
    }


    public void adjustGravity(float newGravityScale) {
        this.setGravityScale(newGravityScale);
    }

    public void createPlayerFixtureWithFriction() {
        SolidFixture fixture = new SolidFixture(this, characterShape);
        fixture.setFriction(270f); 
    }

    

    public void reduceHealth(int damageAmount) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDamageTime > DAMAGE_COOLDOWN) {
            this.health -= damageAmount;
            lastDamageTime = currentTime;

            if (this.health <= 0) {
                health = 0;
                handleDeath();
            }
        } 
        
    }

    public void gainHealth(int i){
        health = health + 20;
        if(health + 20 >100){
            health = 100;
        }
    }

    public int getHealth(){
        return health;
    }

    public int getDamageAmount(){
        return damageAmount;
    }


    public void handleDeath() {
        health = 0;
        System.out.println("Player has died. Restarting game.");
        world.restartGame();
        
    }

    public void updateHitbox() {  
    }

    public void gainArmour(){
        damageAmount -= 20;
    }

}



