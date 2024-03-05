package main;
import city.cs.engine.*;
import javax.swing.*;

import java.awt.*;
import entities.Player;

public class GameView extends UserView {
  private Image background, mountains, graveyard, skulls;
  public static final Font STATUS_FONT = new Font("Serif", Font.ITALIC, (int) 18.5);

  public GameView(World world, int width, int height) {
      super(world, width, height);
      background = new ImageIcon("./assets/images/level-data/background.png").getImage();
      mountains = new ImageIcon("./assets/images/level-data/mountains.png").getImage();
      graveyard = new ImageIcon("./assets/images/level-data/graveyard.png").getImage();
      skulls = new ImageIcon("./assets/images/misc/skulls.gif").getImage();
  }

  @Override
protected void paintBackground(Graphics2D g) {
    Player player = ((GameWorld) getWorld()).getPlayer();
    float playerX = player.getPosition().x;

    // Implemented parallax scrolling, so these are the scrolling factors
    float backgroundParallaxFactor = 0.001f;
    float mountainsParallaxFactor = 2f;
    float graveyardParallaxFactor = 7f;

    // Calculated the new positions for each background layer
    int backgroundX = (int) (playerX * backgroundParallaxFactor) % getWidth();
    int mountainsX = (int) (playerX * mountainsParallaxFactor) % getWidth();
    int graveyardX = (int) (playerX * graveyardParallaxFactor) % getWidth();

    g.drawImage(background, -backgroundX, -55, getWidth()+20, getHeight()+15, this);
    g.drawImage(mountains, -mountainsX -100, 120, getWidth()*6, getHeight()/11*10, this);
    g.drawImage(graveyard, -graveyardX - 200, 270, getWidth()*5, getHeight()/11*9, this);
}


  @Override
  protected void paintForeground(Graphics2D g) {
    Player player = ((GameWorld) getWorld()).getPlayer();
    drawHealthBar(g, player);
    if (player.isArmourOn()) {
        drawArmourBar(g);
    }
    drawKillCounter(g, player);
  }

  private void drawHealthBar(Graphics2D g, Player player) {
    int maxHealthWidth = 200;  
    int healthBarX = 30; 
    int healthBarY = 40;
    drawStatusBar(g, healthBarX, healthBarY, player.getHealth(), 100, Color.RED, "HEALTH: ", maxHealthWidth);
  }

  private void drawArmourBar(Graphics2D g) {
    g.setColor(Color.BLACK);
    g.fillRect(50, 65, 150, 20);
    g.setColor(Color.GRAY);
    g.fillRect(55, 70, 140, 10);
    drawCenteredText(g, "ARMOUR ON!",50, 65, 150, 20, Color.WHITE);
  }

  private void drawKillCounter(Graphics2D g, Player player) {
    int killCounter = player.getKillCounter();
    g.setColor(Color.BLACK);
    g.fillRect(550, 40, 150, 20);
    g.drawImage(skulls, 452, -1, 180, 110, this);
    drawCenteredText(g, "Kill Count: " + killCounter, 550, 40, 150, 20, Color.RED);
  }

  private void drawStatusBar(Graphics2D g, int x, int y, int value, int maxValue, Color barColor, String text,int maxWidth) {
    int barHeight = 20;
    int borderThickness = 5;
    int barInnerWidth = (int) ((float) value / maxValue * (maxWidth - 2 * borderThickness));
    g.setColor(Color.BLACK);
    g.fillRect(x, y, maxWidth, barHeight);
    g.setColor(barColor);
    g.fillRect(x + borderThickness, y + borderThickness, barInnerWidth, barHeight - 2 * borderThickness);
    drawCenteredText(g, text + value, x, y, maxWidth, barHeight, Color.WHITE);
  }

  private void drawCenteredText(Graphics2D g, String text, int x, int y, int width, int height, Color textColor) {
    g.setColor(textColor);
    g.setFont(STATUS_FONT);
    FontMetrics metrics = g.getFontMetrics(STATUS_FONT);
    int textWidth = metrics.stringWidth(text);
    int textX = x + (width - textWidth) / 2;
    int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
    g.drawString(text, textX, textY);
  }
}
