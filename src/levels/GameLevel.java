package levels;

import java.util.List;

import city.cs.engine.*;
import entities.Enemy;
import entities.Player;
import main.Game;

public abstract class GameLevel extends World {
  protected Player player;
  protected List<Enemy> enemies;
  protected Game game;

  public GameLevel(Game game){
      
  }
  public Player getPlayer(){
      return player;
  }

  public abstract boolean isComplete();
}