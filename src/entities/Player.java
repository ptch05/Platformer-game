package entities;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.*;
import input.InputHandler;
import levels.GameLevel;

/**
 * Represents the player character in the game, which is capable of multiple actions
 * such as running, jumping, attacking, and special attacks. The player has health,
 * lives, and can interact with the game world through various states and actions.
 *
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class Player extends Walker {
    private static int xNum = 3;
    private static float yNum = (float)3.85;
    private static int playerSize = 7;
    public static final float WALKING_SPEED = (float)22.2;
    private static final float GRAVITY_FORCE = 2.3f;
    private boolean facingRight = true; 
    private boolean inAir = false;
    private boolean isAttacking = false;
    private boolean armourOn = false;
    private boolean isCrouching = false;
    private int killCounter = 0;
    private Fixture attackFixture;
    private Fixture crouchFixture;
    private Fixture specialAttackFixture;
    private int health;
    private int healthLossAmount = 40;
    private long lastDamageTime;
    private static final long DAMAGE_COOLDOWN = 1000;
    private boolean specialAttackOn = false;
    private boolean specialAttackUsed = false;
    private boolean specialSoundPlayed = false; 
    private int lives = 3;
    private boolean isVictorious = false;
    
    private static final Shape characterShape = new BoxShape(xNum, yNum);
    private static final Shape attackShape = new BoxShape(5.7f, yNum);
    private static final Shape crouchShape = new BoxShape(xNum, 0.2f);
    private static final Shape specialAttackShape = new BoxShape(5.7f, 5.5f);
    private static final BodyImage IDLE_RIGHT = new BodyImage("./assets/images/hero/hero-idle-right.gif", playerSize);
    private static final BodyImage RUN_RIGHT = new BodyImage("./assets/images/hero/hero-run-right.gif", playerSize);
    private static final BodyImage JUMP_RIGHT = new BodyImage("./assets/images/hero/hero-jump-right.gif", playerSize);
    private static final BodyImage ATTACK_RIGHT = new BodyImage("./assets/images/hero/hero-attack-right.gif", playerSize);
    private static final BodyImage HURT_RIGHT = new BodyImage("./assets/images/hero/hero-hurt-right.png",(float) (playerSize*1.3));
    private static final BodyImage CROUCH_RIGHT = new BodyImage("./assets/images/hero/hero-crouch-right.png", (float)(playerSize*1.4));
    private static final BodyImage SPECIAL_ATTACK_RIGHT = new BodyImage("./assets/images/hero/hero-special-attack-right.gif", (float)(playerSize*1.45));
    private static final BodyImage IDLE_LEFT = new BodyImage("./assets/images/hero/hero-idle-left.gif", playerSize);
    private static final BodyImage RUN_LEFT = new BodyImage("./assets/images/hero/hero-run-left.gif", playerSize);
    private static final BodyImage JUMP_LEFT = new BodyImage("./assets/images/hero/hero-jump-left.gif", playerSize);
    private static final BodyImage ATTACK_LEFT = new BodyImage("./assets/images/hero/hero-attack-left.gif", playerSize);
    private static final BodyImage HURT_LEFT = new BodyImage("./assets/images/hero/hero-hurt-left.png",(float) (playerSize*1.3));
    private static final BodyImage CROUCH_LEFT = new BodyImage("./assets/images/hero/hero-crouch-left.png", (float)(playerSize*1.4));
    private static final BodyImage SPECIAL_ATTACK_LEFT = new BodyImage("./assets/images/hero/hero-special-attack-left.gif", (float)(playerSize*1.45));
    
    private BodyImage currentImage;
    private InputHandler inputHandler;
    private GameLevel gameLevel;

    public Player(World world) {
        super(world, characterShape);
        addImage(IDLE_RIGHT);
        addFriction();
        setGravityScale(GRAVITY_FORCE);
        this.health = 100;
    }

    /**
     * Sets the input handler for the player, allowing player actions to be
     * controlled by external inputs.
     * 
     * @param newInputHandler The input handler to process player inputs.
     */
    public void setInputHandler(InputHandler newInputHandler) {
        this.inputHandler = newInputHandler;
    }

    /**
     * Causes the player to run to the right, changing the player's visual representation
     * to a running animation facing right.
     */
    public void runRight() {
        removeAllImages();
        addImage(RUN_RIGHT);
        facingRight = true;
    }
    
    /**
     * Causes the player to run to the left, changing the player's visual representation
     * to a running animation facing left.
     */
    public void runLeft() {
        removeAllImages();
        addImage(RUN_LEFT);
        facingRight = false;
    }

    /**
     * Initiates the player's jump to the right, changing the player's visual representation to
     * a jumping animation facing right and playing the jump sound effect.
     */
    public void jumpRight() {
        removeAllImages();
        addImage(JUMP_RIGHT);
        inAir = true;
        facingRight = true;
        AudioHandler.playJumpSound();
    }

    /**
     * Initiates the player's jump to the left, changing the player's visual representation to
     * a jumping animation facing left and playing the jump sound effect.
     */
    public void jumpLeft() {
        removeAllImages();
        addImage(JUMP_LEFT);
        facingRight = false;
        inAir = true;
        AudioHandler.playJumpSound();
    }

    /**
     * Changes the player's visual representation to an idle stance facing right.
     */
    public void idleRight() {
        removeAllImages();
        addImage(IDLE_RIGHT);
    }

    /**
     * Changes the player's visual representation to an idle stance facing left.
     */
    public void idleLeft() {
        removeAllImages();
        addImage(IDLE_LEFT);
    }

    /**
     * Changes the player's visual representation to a hurt animation facing left and triggers the hurt sound effect.
     */
    public void hurtLeft(){
        removeAllImages();
        addImage(HURT_LEFT);
    }

    /**
     * Changes the player's visual representation to a hurt animation facing right and triggers the hurt sound effect.
     */
    public void hurtRight(){
        removeAllImages();
        addImage(HURT_RIGHT);
    }
    
     /**
     * Checks if the player is currently facing right.
     *
     * @return true if the player is facing right, false otherwise.
     */
    public boolean isFacingRight() {
        return facingRight;
    }

    /**
     * Checks if the player is currently in the air, implying a jump or fall.
     *
     * @return true if the player is in the air, false otherwise.
     */
    public boolean isInAir() {
        return inAir;
    }
    
    public void update() {
        // Simulate gravity by applying a force when the player is in the air.
        if (isInAir()) {
            this.applyForce(new Vec2(0, GRAVITY_FORCE));
        }
        
        if (this.health <= 0) {
            handleDeath();
        }
    }
    
    /**
     * Initiates the player's attack, creating an attack hitbox and playing the attack sound.
     */
    public void attack() {
        if (!isAttacking()) {
            isAttacking = true;
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
        attackFixture = new SolidFixture(this, attackShape); // Create a new attack hitbox
        
    }

    /**
     * Ends the player's attack, removing the attack hitbox.
     */
    public void endAttack() {
        if (isAttacking()) {
            isAttacking = false;
            if (attackFixture != null) {
                attackFixture.destroy(); // Remove the attack hitbox using destroy()
                attackFixture = null; // Clear the reference after destruction
            }
        }
    }

    /**
     * Checks if the player is currently attacking.
     *
     * @return true if the player is in the process of attacking, false otherwise.
     */
    public boolean isAttacking() {
        return isAttacking;
    }

    /**
     * Adjusts the player's gravity to the specified scale.
     * 
     * @param newGravityScale The new gravity scale to be set for the player.
     */
    public void adjustGravity(float newGravityScale) {
        this.setGravityScale(newGravityScale);
    }

    /**
     * Applies friction to the player to simulate realistic movement.
     */
    public void addFriction() {
        SolidFixture fixture = new SolidFixture(this, characterShape);
        fixture.setFriction(300f); 
    }

    /**
     * Reduces the player's health by a specified amount and plays a hurt sound,
     * implementing a cooldown between damages.
     * 
     * @param damageAmount The amount of health to be reduced from the player.
     */
    public void reduceHealth(int damageAmount) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDamageTime > DAMAGE_COOLDOWN) {
            this.health -= damageAmount;
            lastDamageTime = currentTime;
            AudioHandler.playHurtSound();
        
            if (this.health <= 0) {
                health = 0;
                handleDeath();
            }
        } 
        
    }

    /**
     * Increases the player's health by a specified amount, not exceeding the maximum health.
     * 
     * @param amount The amount of health to increase.
     */
    public void gainHealth(int amount){
        health += 20;
        if(health + 20 >100){
            health = 100;
        }
    }

    public int getHealth(){
        return health;
    }

    public int getHealthLossAmount(){
        return healthLossAmount;
    }

    public int getLives(){
        return lives;
    }

    public void loseLives(){
        lives--;
    }

    public void gainLives(){
        lives++;
    }

    public void setGameLevel(GameLevel gameLevel) {
        this.gameLevel = gameLevel;
    }

    public void handleDeath() {
        health = 0;
        gameLevel.restartGame();
        loseLives();
    }

    public boolean gainArmour() {
        if (!armourOn) { 
            healthLossAmount /= 2; 
            armourOn = true;     
            return true;         
        }
        return false; 
    }

    public boolean loseArmour(){
        if(armourOn){
            healthLossAmount *=2 ;
            armourOn = false;
            return true;
        }
        return false;
    }

    /**
     * Checks if the player is currently wearing armour.
     *
     * @return true if the player is wearing armour, false otherwise.
     */
    public boolean isArmourOn() {
        return armourOn;
    }

    /**
     * Checks if the player's special attack is currently active.
     *
     * @return true if the special attack is active, false otherwise.
     */
    public boolean isSpecialAttackOn(){
        return specialAttackOn;
    }
    public boolean gainSpecialAttack() {
        if (killCounter == 5 && !specialAttackOn) {
            specialAttackOn = true;
            if(!specialSoundPlayed){
                AudioHandler.playGainSpecialSound(); // Plays sound only when the special attack is gained
                specialSoundPlayed = true;
            }
            return true;
        }
        return false; 
    }

    /**
     * Checks if the player is currently executing a special attack.
     *
     * @return true if the player is performing a special attack, false otherwise.
     */
    public boolean isSpecialAttacking(){
        return specialAttackUsed;
    }

    public void specialAttack(){
        if(specialAttackOn && !specialAttackUsed){
            if (!isSpecialAttacking()) {
                specialAttackUsed = true;
                removeAllImages();
                if (facingRight) {
                    currentImage = SPECIAL_ATTACK_RIGHT;
                } else {
                    currentImage = SPECIAL_ATTACK_LEFT;
                }
                addImage(currentImage);
                createSpecialAttackHitbox(specialAttackShape);
                AudioHandler.playSpecialAttackSound();
                
            }
        }
    }

    private void createSpecialAttackHitbox(Shape specialAttackShape) {
        specialAttackFixture = new SolidFixture(this, specialAttackShape); // Create a new special attack hitbox
    }

    public void endSpecialAttack(){
        if(isSpecialAttacking()){
            specialAttackFixture.destroy(); 
            specialAttackOn = false;
            specialAttackUsed = false;

            removeAllImages();
            if (facingRight) {
                addImage(IDLE_RIGHT);
            } else {
                addImage(IDLE_LEFT);
            } // Changes player state back to idle after doing the attack
        }
    }

    public int getKillCounter(){
        return killCounter;
    }
    
    public void addKill() {
        killCounter++;
        if (killCounter == 5) {
            gainSpecialAttack();
        }
    }

    /**
     * Checks if the player is currently in a crouching position.
     *
     * @return true if the player is crouching, false otherwise.
     */
    public boolean isCrouching(){
        return isCrouching;
        
    }

    public void crouch() {
        if (!isCrouching()) {
            isCrouching = true;
            removeAllImages();
            if (facingRight) {
                currentImage = CROUCH_RIGHT;
                createCrouchHitbox(crouchShape);
            } else {
                currentImage = CROUCH_LEFT;
                createCrouchHitbox(crouchShape);
            }
            addImage(currentImage);
            AudioHandler.playCrouchSound();
        }
    }

    private void createCrouchHitbox(Shape crouchShape) {
        crouchFixture = new SolidFixture(this, crouchShape); // Create a new crouch hitbox
    }

    /**
     * Ends the player's crouching action, reverting the player to a standing position and removing the crouch hitbox.
     */
    public void endCrouch() {
        if (isCrouching) {
            isCrouching = false;
            if (crouchFixture != null) {
                crouchFixture.destroy(); // Remove the attack hitbox using destroy()
                crouchFixture = null; // Clear the reference after destruction
            }
        }
    }

    /**
     * Checks if the player has achieved a victorious state, indicating the completion of a level.
     *
     * @return true if the player is victorious, false otherwise.
     */
    public boolean isVictorious() {
        return isVictorious;
    }

    /**
     * Sets the player's victorious state. When set to true, it indicates that the player
     * has completed the level. This state triggers
     * a sound and transitions to new game levels, or a "YOU WIN" screen.
     * 
     * @param victorious A boolean indicating whether the player has won (true) or not (false).
     */
    public void setVictorious(boolean victorious) {
        this.isVictorious = victorious;
    }
    
}