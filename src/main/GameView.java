package main;
import city.cs.engine.*;
import javax.swing.*;

import java.awt.*;
import entities.Player;
import levels.GameLevel;

public class GameView extends UserView {
  private final Image skulls, bloodthirsty, win;
  private Image background, middleground, foreground;
  private static final Font STATUS_FONT = new Font("Serif", Font.ITALIC, (int) 18.5);
  private GameLevel gameLevel;

  public GameView(GameLevel gameLevel, int width, int height) {
      super(gameLevel, width, height);
      this.gameLevel = gameLevel;
      skulls = new ImageIcon("./assets/images/misc/skulls.gif").getImage();
      bloodthirsty = new ImageIcon("./assets/images/misc/bloodthirsty.gif").getImage();
      win = new ImageIcon("./assets/images/misc/win.png").getImage();
      loadBackgroundImages();
  }

  private void loadBackgroundImages() {
    String levelName = gameLevel.getLevelName(); // Get the name of the current level
    ImageIcon bgIcon, midIcon, fgIcon;
    switch (levelName) {
        case "Level 1":
            bgIcon = new ImageIcon("./assets/images/level-data/level1/background.png");
            background = bgIcon.getImage();
            midIcon = new ImageIcon("./assets/images/level-data/level1/mountains.png");
            middleground = midIcon.getImage();
            fgIcon = new ImageIcon("./assets/images/level-data/level1/graveyard.png");
            foreground = fgIcon.getImage();
            break;
        /*case "Level 2":
            background = new ImageIcon("./assets/images/level-data/level2/background1.png").getImage();
            middleground = new ImageIcon("./assets/images/level-data/level2/background2.png").getImage();
            foreground = new ImageIcon("./assets/images/level-data/level2/background3.png").getImage();
            System.out.println("!");
            break;
        case "Level 3":
            background = new ImageIcon("./assets/images/level-data/level3/background.png").getImage();
            middleground = new ImageIcon("./assets/images/level-data/level3/pillars.png").getImage();
            foreground = new ImageIcon("./assets/images/level-data/level3/ceiling.png").getImage();*/

    }
  }

  public void updateLevel(GameLevel newLevel) {
      this.gameLevel = newLevel;
      loadBackgroundImages();
      repaint(); // Refresh the view with new backgrounds
  }

    @Override
    protected void paintBackground(Graphics2D g) {
    Player player = gameLevel.getPlayer();
    if (player.isVictorious()) {
      drawWin(g);
      return;
    } else{
      float playerX = player.getPosition().x;

      // Implemented parallax scrolling, so these are the scrolling factors
      float backgroundParallaxFactor = 0.001f;
      float middlegroundParallaxFactor = 2f;
      float foregroundParallaxFactor = 7f;

      // Calculated the new positions for each background layer
      int backgroundX = (int) (playerX * backgroundParallaxFactor) % getWidth();
      int middlegroundX = (int) (playerX * middlegroundParallaxFactor) % getWidth();
      int foregroundX = (int) (playerX * foregroundParallaxFactor) % getWidth();

      g.drawImage(background, -backgroundX, -55, getWidth()+20, getHeight()+15, this);
      g.drawImage(middleground, -middlegroundX -100, 120, getWidth()*6, getHeight()/11*10, this);
      g.drawImage(foreground, -foregroundX - 200, 270, getWidth()*5, getHeight()/11*9, this);

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
}
