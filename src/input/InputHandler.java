package input;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.Timer;
import java.util.TimerTask;

import entities.Player;
import levels.GameLevel;
public class InputHandler implements KeyListener {
    private boolean keyWPressed = false;
    private boolean keyAPressed = false;
    private boolean keyDPressed = false;
    private boolean keySPressed = false;
    private boolean keyKPressed = false;
    private boolean keyGPressed = false;
    private Player player;
    private GameLevel gameLevel;

    public InputHandler(Player p, GameLevel gameLevel) {
        player = p;
        this.gameLevel = gameLevel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // N.A.
    }

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
    
    public void setPlayer(Player newPlayer) {
        this.player = newPlayer;
    }

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
