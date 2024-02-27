package main;
import javax.swing.JFrame;

import input.InputHandler;

public class Game{

	 /** The World in which the bodies move and interact. */
	 private GameWorld world;

	 /** A graphical display of the world (a specialised JPanel). */
	 private GameView view;

	 /** Initialise a new test.Game. */
	 public Game() {

			 //make the world
			 GameWorld world = new GameWorld();

			 //make the view
			 view = new GameView(world,800,600);

			 InputHandler controller = new InputHandler(world.getPlayer());
       view.addKeyListener(controller);
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

	 /** Run the game. */
	 public static void main(String[] args) {

			 new Game();
	 }
}
