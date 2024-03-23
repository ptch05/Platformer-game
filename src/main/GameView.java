package main;
import city.cs.engine.*;
import javax.swing.*;

import java.awt.*;
import entities.Player;
import levels.GameLevel;

public class GameView extends UserView {
  private Image background, middleground1, middleground2, foreground, skulls, bloodthirsty, win;
  private static final Font STATUS_FONT = new Font("Serif", Font.ITALIC, (int) 18.5);
  private GameLevel gameLevel;

  public GameView(GameLevel gameLevel, int width, int height) {
      super(gameLevel, width, height);
      this.gameLevel = gameLevel;
      loadBackgroundImages(gameLevel.getLevelName());
      skulls = new ImageIcon("./assets/images/misc/skulls.gif").getImage();
      bloodthirsty = new ImageIcon("./assets/images/misc/bloodthirsty.gif").getImage();
      win = new ImageIcon("./assets/images/misc/win.png").getImage();
      
  }

  private void loadBackgroundImages(String levelName){
    switch (levelName) {
      case "Level1":
        background = new ImageIcon("./assets/images/level-data/level1/background.png").getImage();
        middleground1 = new ImageIcon("./assets/images/level-data/level1/mountains.png").getImage();
        foreground = new ImageIcon("./assets/images/level-data/level1/graveyard.png").getImage();
        break;

      case "Level2":
        background = new ImageIcon("./assets/images/level-data/level2/background.png").getImage();
        middleground1 = new ImageIcon("./assets/images/level-data/level2/middleground1.png").getImage();
        middleground2 = new ImageIcon("./assets/images/level-data/level2/middleground2.png").getImage();
        foreground = new ImageIcon("./assets/images/level-data/level2/foreground.png").getImage();
        break;

      case "Level3":
        background = new ImageIcon("./assets/images/level-data/level3/background.png").getImage();
        middleground1 = new ImageIcon("./assets/images/level-data/level3/pillars.png").getImage();
        foreground = new ImageIcon("./assets/images/level-data/level3/ceiling.png").getImage();

      break;
    }
  }



    @Override
    protected void paintBackground(Graphics2D g) {
    Player player = gameLevel.getPlayer();
    if (player.isVictorious()) {
      drawWin(g);
      return;
    } else{
      
      drawBackground(gameLevel.getLevelName(), g);

      
    }
}

private void drawBackground(String levelName, Graphics2D g){
  Player player = gameLevel.getPlayer();
  float playerX = player.getPosition().x;

      // Implemented parallax scrolling, so these are the scrolling factors
      float backgroundParallaxFactor = 1f;
      float middlegroundParallaxFactor = 2.5f;
      float middleground2ParallaxFactor = 5f;
      float foregroundParallaxFactor = 7f;

      // Calculated the new positions for each background layer
      int backgroundX = (int) (playerX * backgroundParallaxFactor) % getWidth();
      int middlegroundX = (int) (playerX * middlegroundParallaxFactor) % getWidth();
      int middleground2X = (int) (playerX * middleground2ParallaxFactor) % getWidth();
      int foregroundX = (int) (playerX * foregroundParallaxFactor) % getWidth();


      switch (levelName) {
        case "Level1":
        g.drawImage(background, 0, -55, getWidth()+20, getHeight()+15, this);
        g.drawImage(middleground1, -middlegroundX -100, 120, getWidth()*6, getHeight()/11*10, this);
        g.drawImage(foreground, -foregroundX - 200, 270, getWidth()*5, getHeight()/11*9, this);
          break;
  
        case "Level2":
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(middleground1, -middlegroundX -100, -40, getWidth()*6, getHeight(), this);
        g.drawImage(middleground2, -middleground2X -100, 5, getWidth()*6, getHeight(), this);
        g.drawImage(foreground, -foregroundX*(int)1.15 - 200, 0, getWidth()*5, getHeight(), this);
          break;
  
        case "Level3":
        g.drawImage(background, -backgroundX-100, 55, getWidth()*5, getHeight(), this);
        g.drawImage(middleground1, -middlegroundX -100, 60, getWidth()*6, getHeight()/11*10, this);
        g.drawImage(foreground, -foregroundX - 200, -55, getWidth()*5, getHeight()/11*9, this);
        break;
      }
    }


  @Override
  protected void paintForeground(Graphics2D g) {
    Player player = gameLevel.getPlayer();
    if (!player.isVictorious()) {
      drawHealthBar(g, player);
      if (player.isArmourOn()) {
          drawArmourBar(g);
      } if(player.isSpecialAttackOn()){
        drawBloodthirsty(g);
      } 
      drawKillCounter(g, player);
    }
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
    g.drawImage(skulls, 475, 10, 140, 90, this);
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

  private void drawBloodthirsty(Graphics2D g){
    g.drawImage(bloodthirsty, 535, 75, 155, 45, this);
  }

  private void drawWin(Graphics2D g){
    g.drawImage(win, 0, 0, getWidth(), getHeight(), this);
  }

  public void setGameLevel(GameLevel gameLevel) {
    this.gameLevel = gameLevel;
    loadBackgroundImages(gameLevel.getLevelName()); // Reloads background images for the new level
    this.repaint(); 
  }


}
