package main;
import city.cs.engine.*;
import javax.swing.*;
import java.awt.*;

public class GameView extends UserView {
  private Image background;
  private Image mountains;
  private Image graveyard;

  public GameView(GameWorld world, int width, int height) {
      super(world, width, height);

      background = new ImageIcon("assets/images/level-data/background.png").getImage();
      mountains = new ImageIcon("assets/images/level-data/mountains.png").getImage();
      graveyard = new ImageIcon("assets/images/level-data/graveyard.png").getImage();
  }

  @Override
  protected void paintBackground(Graphics2D g) {
    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    g.drawImage(mountains, 0, 100, getWidth(), getHeight()/11*10, this);
    g.drawImage(graveyard, 0, 200, getWidth(), getHeight()/11*9, this);
  }

  @Override
  protected void paintForeground(Graphics2D g){

  }
}
