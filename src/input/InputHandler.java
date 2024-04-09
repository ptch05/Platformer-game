package input;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;
import entities.Player;
import levels.GameLevel;

/**
 * Handles keyboard input for controlling a Player object in the game.
 * It listens for specific key events to perform actions such as moving,
 * jumping, attacking, and special attacks. This class is responsible for
 * linking player actions with corresponding keyboard keys.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class InputHandler implements KeyListener {
    private boolean keyWPressed = false;
    private boolean keyAPressed = false;
    private boolean keyDPressed = false;
    private boolean keySPressed = false;
    private boolean keyKPressed = false;
    private boolean keyGPressed = false;
    private Player player;
    private GameLevel gameLevel;

    /**
     * Constructs an InputHandler with a specified Player and GameLevel.
     * 
     * @param p The player to be controlled.
     * @param gameLevel The current level of the game.
     */
    public InputHandler(Player p, GameLevel gameLevel) {
        player = p;
        this.gameLevel = gameLevel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // N.A.
    }

    /**
     * Handles key press events to perform player actions. Defines behavior
     * for moving left, right, jumping, attacking, and special attacks based
     * on the keys pressed.
     * 
     * @param e The KeyEvent triggered by pressing a key.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        Player player = gameLevel.getPlayer(); 
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W:
                player.jump(20);
                player.adjustGravity(3.3f);
                if (player.isFacingRight()) {
                    player.jumpRight();
                } else {
                    player.jumpLeft();
                }
                keyWPressed = true;
                break;
            case KeyEvent.VK_A:
                if (!keyDPressed) { // Don't start walking left if D is already pressed
                    player.startWalking(-Player.WALKING_SPEED);
                    player.runLeft();
                }
                keyAPressed = true;
                break;

            case KeyEvent.VK_S:
                keySPressed = true;
                if(!keyAPressed || !keySPressed){
                    player.stopWalking();
                    player.crouch();
                } else{
                    player.crouch();
                }
                break;

            case KeyEvent.VK_D:
                if (!keyAPressed) { // Don't start walking right if A is already pressed
                    player.startWalking(Player.WALKING_SPEED);
                    player.runRight();
                }
                keyDPressed = true;
                break;

            case KeyEvent.VK_K:
                // Stop running when attacking
                stopMoving();
                keyKPressed = true;
                player.attack();
                break;
            case KeyEvent.VK_G:
                stopMoving();
                keyGPressed = true;
                player.specialAttack();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        player.endSpecialAttack();
                    }
                }, 650); // Special attack no matter what is finished within 650ms and player can't keep doing it
                break;
        }
    }

    /**
     * Handles key release events to stop or modify player actions. This includes
     * stopping movement, ending attacks, or changing back to idle states.
     * 
     * @param e The KeyEvent triggered by releasing a key.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        Player player = ((GameLevel)gameLevel).getPlayer(); 
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W:
                if(keyAPressed){
                    player.runLeft();
                } else if(keyDPressed){
                    player.runRight();
                } // These are here as otherwise when W is released, the player doesn't return to normal running animations
                break;
            case KeyEvent.VK_A:
                player.stopWalking();
                keyAPressed = false;
                break;
            case KeyEvent.VK_S:
                player.endCrouch();
                break;
            case KeyEvent.VK_D:
                player.stopWalking();
                keyDPressed = false;
                break;
            case KeyEvent.VK_K:
                player.endAttack();
                break;
            case KeyEvent.VK_G:
                player.endSpecialAttack();
                break;
        }   
        // If no keys are pressed and the player is not moving horizontally, this sets idle animation based on the last direction the player was facing
        if (!keyAPressed && !keyDPressed) {
            if (player.isFacingRight()) {
                player.idleRight();
            } else {
                player.idleLeft();
            }
        }
    }
    
    /**
     * Sets a new Player object for the input handler to control. This method is
     * used to update the player instance being controlled when switching levels.
     * 
     * @param newPlayer The new Player object to control.
     */
    public void setPlayer(Player newPlayer) {
        this.player = newPlayer;
    }

    /**
     * Stops the player's movement and resets the key pressed states. This method
     * is called before performing actions that require the player to stop, such as
     * attacking or executing a special attack.
     */
    private void stopMoving(){
        if (!keyAPressed || !keyDPressed) {
            player.stopWalking();
            if (keyAPressed) {
                player.idleLeft();
            } else if (keyDPressed) {
                player.idleRight();
            }
            keyAPressed = false;
            keyDPressed = false;
        }
    }
}
