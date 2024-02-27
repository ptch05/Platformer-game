package utilities;

import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import entities.Player;
import main.GameView;

public class Tracker implements StepListener {
  private GameView view;
  private Player player;
  public Tracker(GameView view, Player player) {
      this.player = player;
      this.view = view;
  }
  public void preStep(StepEvent e) {}
  public void postStep(StepEvent e) {
      view.setCentre(player.getPosition());
  }
}