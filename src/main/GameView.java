package main;
import city.cs.engine.*;
import javax.swing.*;

import java.awt.*;
import entities.Player;

public class GameView extends UserView {
  private Image background;
  private Image mountains;
  private Image graveyard;
  public static final Font STATUS_FONT = new Font("Serif", Font.ITALIC, (int) 18.5);
  private float backgroundX, mountainsX, graveyardX;
 

  public GameView(GameWorld world, int width, int height) {
      super(world, width, height);
      background = new ImageIcon("assets/images/level-data/background.png").getImage();
      mountains = new ImageIcon("assets/images/level-data/mountains.png").getImage();
      graveyard = new ImageIcon("assets/images/level-data/graveyard.png").getImage();
  }

  @Override
  protected void paintBackground(Graphics2D g) {
      g.drawImage(background, (int)backgroundX, 0, getWidth(), getHeight(), this);
      g.drawImage(mountains, (int)mountainsX, 100, getWidth(), getHeight()/11*10, this);
      g.drawImage(graveyard, (int)graveyardX, 250, getWidth(), getHeight()/11*9, this);
  }

    @Override
  protected void paintForeground(Graphics2D g) {
      Player player = ((GameWorld) getWorld()).getPlayer();
      int maxHealthWidth = 200; 
      int healthBarHeight = 20; 
      int borderThickness = 5;

      int health = player.getHealth();
      int maxHealth = 100; 

      // Calculate the current width of the health bar based on player's health
      int currentHealthWidth = (int) ((health / (float) maxHealth) * (maxHealthWidth - 2 * borderThickness));
      int healthBarX = 30;
      int healthBarY = 40;
      g.setColor(Color.BLACK);
      g.fillRect(healthBarX, healthBarY, maxHealthWidth, healthBarHeight);
      g.setColor(Color.RED);
      g.fillRect(healthBarX + borderThickness, healthBarY + borderThickness, currentHealthWidth, healthBarHeight - 2 * borderThickness);
      g.setColor(Color.WHITE);
      g.setFont(STATUS_FONT);
      String healthText = "Health: " + health;
      
      // Calculate the width of the text so we can position it centered on the health bar
      FontMetrics metrics = g.getFontMetrics(STATUS_FONT);
      int textWidth = metrics.stringWidth(healthText);
      int textX = healthBarX + (maxHealthWidth - textWidth) / 2;
      int textY = healthBarY + ((healthBarHeight - metrics.getHeight()) / 2) + metrics.getAscent();
      g.drawString(healthText, textX, textY);
  }


}
