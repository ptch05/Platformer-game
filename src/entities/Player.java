package entities;

import org.jbox2d.common.Vec2;

import audio.AudioHandler;
import city.cs.engine.*;
import input.InputHandler;


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
    private boolean isVictorious = false; 
    private int lives = 3;
    
    private static final Shape characterShape = new BoxShape(xNum, yNum);
    private static final Shape attackShape = new BoxShape(5.7f, yNum);
    private static final Shape crouchShape = new BoxShape(xNum, 4f);
    private static final Shape specialAttackShape = new BoxShape(5.7f, 5f);
    private static final BodyImage IDLE_RIGHT = new BodyImage("./assets/images/hero/hero-idle-right.gif", playerSize);
    private static final BodyImage RUN_RIGHT = new BodyImage("./assets/images/hero/hero-run-right.gif", playerSize);
    private static final BodyImage JUMP_RIGHT = new BodyImage("./assets/images/hero/hero-jump-right.gif", playerSize);
    private static final BodyImage ATTACK_RIGHT = new BodyImage("./assets/images/hero/hero-attack-right.gif", playerSize);
    private static final BodyImage HURT_RIGHT = new BodyImage("./assets/images/hero/hero-hurt-right.png",(float) (playerSize*1.3));
    private static final BodyImage CROUCH_RIGHT = new BodyImage("./assets/images/hero/hero-crouch-right.png", (float)(playerSize*1.4));
    private static final BodyImage SPECIAL_ATTACK_RIGHT = new BodyImage("./assets/images/hero/hero-special-attack-right.gif", (float)(playerSize*1.6));
    private static final BodyImage IDLE_LEFT = new BodyImage("./assets/images/hero/hero-idle-left.gif", playerSize);
    private static final BodyImage RUN_LEFT = new BodyImage("./assets/images/hero/hero-run-left.gif", playerSize);
    private static final BodyImage JUMP_LEFT = new BodyImage("./assets/images/hero/hero-jump-left.gif", playerSize);
    private static final BodyImage ATTACK_LEFT = new BodyImage("./assets/images/hero/hero-attack-left.gif", playerSize);
    private static final BodyImage HURT_LEFT = new BodyImage("./assets/images/hero/hero-hurt-left.png",(float) (playerSize*1.3));
    private static final BodyImage CROUCH_LEFT = new BodyImage("./assets/images/hero/hero-crouch-left.png", (float)(playerSize*1.4));
    private static final BodyImage SPECIAL_ATTACK_LEFT = new BodyImage("./assets/images/hero/hero-special-attack-left.gif", (float)(playerSize*1.6));
    
    private BodyImage currentImage;
    private InputHandler inputHandler;

    public Player(World world) {
        super(world, characterShape);
        addImage(IDLE_RIGHT);
        addFriction();
        setGravityScale(GRAVITY_FORCE);
        this.health = 100;
    }

    public void setInputHandler(InputHandler newInputHandler) {
        this.inputHandler = newInputHandler;
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
    }

    public void idleLeft() {
        removeAllImages();
        addImage(IDLE_LEFT);
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
        }
        
        if (this.health <= 0) {
            handleDeath();
        }
    }
    

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
    
    public void endAttack() {
        if (isAttacking()) {
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

    public void addFriction() {
        SolidFixture fixture = new SolidFixture(this, characterShape);
        fixture.setFriction(270f); 
    }

    

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

    public void gainHealth(int i){
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


   public void handleDeath() {
        health = 0;
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

    public boolean isArmourOn() {
        return armourOn;
    }

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

    private void createSpecialAttackHitbox(Shape attackShape) {
        specialAttackFixture = new SolidFixture(this, specialAttackShape); // Create a new special attack hitbox
    }

    public void endSpecialAttack(){
        if(isSpecialAttacking()){
            specialAttackFixture.destroy(); 
            specialAttackOn = false;
            specialAttackUsed = false;
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
        if (crouchFixture != null) {
            crouchFixture.destroy(); // Use destroy() method directly on the fixture
            crouchFixture = null; // Clear the reference after destruction
        }
        crouchFixture = new SolidFixture(this, crouchShape); // Create a new attack hitbox
        
    }
    
    public void endCrouch() {
        if (isCrouching) {
            isCrouching = false;
            if (crouchFixture != null) {
                crouchFixture.destroy(); // Remove the attack hitbox using destroy()
                crouchFixture = null; // Clear the reference after destruction
            }
        }
    }

    public boolean isVictorious(){
        return isVictorious;
    }

    public void setVictorious(){
        isVictorious = true;
    }
}