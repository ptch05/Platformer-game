package input;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import city.cs.engine.BodyImage;
import entities.Player;
import main.GameWorld;

public class InputHandler implements KeyListener{
  private boolean keyWPressed = false;
  private boolean keyAPressed = false;
  private boolean keySPressed = false;
  private boolean keyDPressed = false;
  

  @Override
  public void keyTyped(KeyEvent e) {
    // N.A.
  }

  private Player player;
  public InputHandler (Player player){
    this.player = player;
}

  @Override
  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    switch (code) {
      case KeyEvent.VK_W:
      System.out.println("Pressed W");
      break;

      case KeyEvent.VK_A:
      System.out.println("Pressed A");
        
      break;

      case KeyEvent.VK_S:
      System.out.println("Pressed S");


      break;

      case KeyEvent.VK_D:
      System.out.println("Pressed D");
        
      break;
        
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    int code = e.getKeyCode();
    switch (code) {
      case KeyEvent.VK_W:
      System.out.println("unPressed W");
        
      break;

      case KeyEvent.VK_A:
      System.out.println("unPressed A");
        
      break;

      case KeyEvent.VK_S:
      System.out.println("unPressed S");

      break;

      case KeyEvent.VK_D:
      System.out.println("unPressed D");
        
      break;
        
    }
  }
  
}
