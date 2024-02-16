package main;

import city.cs.engine.*;

public class Main {
    public static void main(String[] args) {
      GameWorld world = new GameWorld();
      GameView view = new GameView(world, 800, 600);  
      GameFrame frame = new GameFrame(view);
      world.start();
}
}