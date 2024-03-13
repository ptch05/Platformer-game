package main;

import javax.swing.JFrame;

import input.InputHandler;
import levels.*;
import menu.Menu;

public class Game {
	private JFrame frame;
	private GameLevel level;
	private GameView view;
	private InputHandler inputHandler;


	public Game() {
			//create a Java window (frame) and add the game
			frame = new JFrame("GothicVania Cemetery");

			// enable the frame to quit the application
			// when the x button is pressed
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationByPlatform(true);
			// don't let the frame be resized
			frame.setResizable(false);
			// size the frame to fit the world view

			level = new Level1(this);
			view = new GameView(level, 800, 600);
			
			Menu menu = new Menu(frame, 800, 600, level);
			frame.add(view);
			frame.pack();
			// finally, make the frame visible
			frame.setVisible(true);

			
	}

	 public void goToNextLevel(){
			if (level instanceof Level1){
					level.stop();
					level = new Level2(this);
					//level now refer to the new level
					view.setWorld(level);
					inputHandler.setPlayer(level.getPlayer());
					level.start();
			}
			else if (level instanceof Level2){
				level.stop();
				level = new Level3(this);
				view.setWorld(level);
				inputHandler.setPlayer(level.getPlayer());
				level.start();
			}
    }

	public static void main(String[] args) {
		  new Game();			
	}
}