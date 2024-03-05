package main;
import javax.swing.JFrame;

import entities.Player;
import input.InputHandler;
import utilities.PlayerListener;

public class Game{

	 /** The World in which the bodies move and interact. */
	 private GameWorld world;

	 /* A graphical display of the world (a specialised JPanel). */
	 private GameView view;

	 private Player player;

	 /* Initialise a new test.Game. */
	 public Game() {

			 //make the world
			 world = new GameWorld();

			 //make the view
			 view = new GameView(world,800,600);

			 player = world.getPlayer();

			 InputHandler controller = new InputHandler(world);
       view.addKeyListener(controller);

			
			 // Create the player listener and add it to the world
			 PlayerListener playerListener = new PlayerListener(world, view);
       world.addStepListener(playerListener);

			 //create a Java window (frame) and add the game
			 final JFrame frame = new JFrame("Gothic Cemetery");
			 frame.add(view);

			 // enable the frame to quit the application
			 // when the x button is pressed
			 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			 frame.setLocationByPlatform(true);
			 // don't let the frame be resized
			 frame.setResizable(false);
			 // size the frame to fit the world view
			 frame.pack();
			 // finally, make the frame visible
			 frame.setVisible(true);

			 // start our game world simulation!
			 world.start();
			 view.requestFocus();
	 }

	 /* Run the game. */
	 public static void main(String[] args) {
			 new Game();
	 }
}
