package main;

import javax.swing.JFrame;

import entities.Player;
import input.InputHandler;
import levels.*;
import menu.Menu;

public class Game {
	private JFrame frame;
	private GameLevel gameLevel;
	private GameView view;
	private Player player;
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
			Menu menu = new Menu(frame, 600, 800, this); // Pass 'this' to the Menu
			frame.add(menu); // Add the menu to the frame
	
			frame.pack();
			frame.setVisible(true);
	
	}
	

	 public void goToNextLevel(){
			if (gameLevel instanceof Level1){
					gameLevel.stop();
					GameLevel gameLevel = new Level2(this);
					//level now refer to the new level
					view.setWorld(gameLevel);
					inputHandler.setPlayer(gameLevel.getPlayer());
					gameLevel.start();
			}
			else if (gameLevel instanceof Level2){
					gameLevel.stop();
					GameLevel gameLevel = new Level3(this);
					gameLevel.start();
			}
	 }

	public static void main(String[] args) {
		  new Game();			
	}
}