package input;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;
import entities.Player;
import main.GameWorld;

public class InputHandler implements KeyListener {
    private boolean keyWPressed = false;
    private boolean keyAPressed = false;
    private boolean keyDPressed = false;
    private boolean isAnimating = false;
    private Player player;
    private GameWorld world;

    public InputHandler(GameWorld world) {
        this.world = world;
    }

    public boolean isKeyWPressed() {
        return keyWPressed;
    }

    public boolean isKeyAPressed() {
        return keyAPressed;
    }

    public boolean isKeyDPressed() {
        return keyDPressed;
    }

    private Player getPlayer() {
        return (Player) world.getPlayer();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // N.A.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Player player = getPlayer(); 
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W:
                // Check if not already in animation
                if (!isAnimating) {
                    // Set animation state to true
                    isAnimating = true;

                    // Perform jump and animation
                    player.jump(20);
                    player.adjustGravity(3.3f);
                    if (player.isFacingRight()) {
                        player.jumpRight();
                    } else {
                        player.jumpLeft();
                    }

                    // Schedule a task to reset animation state after animation duration
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // Reset animation state to false
                            isAnimating = false;
                            // Cancel this task
                            timer.cancel();
                        }
                    }, 2);
                }
                break;
            case KeyEvent.VK_A:
                if (!keyDPressed) { // Don't start walking left if D is already pressed
                    player.startWalking(-Player.WALKING_SPEED);
                    player.runLeft();
                }
                keyAPressed = true;
                break;
            case KeyEvent.VK_D:
                if (!keyAPressed) { // Don't start walking right if A is already pressed
                    player.startWalking(Player.WALKING_SPEED);
                    player.runRight();
                }
                keyDPressed = true;
                break;

            case KeyEvent.VK_K:
                player.attack();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Player player = getPlayer(); 
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W:
                if(keyAPressed){
                    player.runLeft();
                } else if(keyDPressed){
                    player.runRight();
                }
                break;
            case KeyEvent.VK_A:
                player.stopWalking();
                player.idleLeft();
                keyAPressed = false;
                break;
            case KeyEvent.VK_D:
                player.stopWalking();
                player.idleRight();
                keyDPressed = false;
                break;
            case KeyEvent.VK_K:
                player.endAttack();
                if(keyDPressed){
                    player.runRight();
                }else if(keyAPressed){
                    player.runLeft();
                }
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
}
