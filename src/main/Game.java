package main;

import javax.swing.JFrame;

import city.cs.engine.World;
import entities.Player;
import input.InputHandler;
import levels.*;
import menu.Menu;
import utilities.PlayerListener;

public class Game {
	private JFrame frame;
	private GameLevel currentLevel;
	private GameView view;
	private World world;
	private Player player;
	private InputHandler inputHandler;
	
	public Game() {
		initializeFrame();
		initializeMenu();
	}

	private void initializeFrame() {
		//create a Java window (frame) and add the game
		frame = new JFrame("GothicVania Cemetery");
		// enable the frame to quit the application
		// when the x button is pressed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		// don't let the frame be resized
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
	}

	private void initializeMenu() {
		Menu menu = new Menu(frame, 800, 600, this);
		frame.add(menu);
		frame.pack();
	}

	public void startGame(){
		setupLevel(new Level2(this));
	}

	private void setupLevel(GameLevel level){
		frame.getContentPane().removeAll();
		currentLevel = level;
		view = new GameView(currentLevel, 800, 600);

		frame.add(view);
		frame.revalidate();
		frame.repaint();
		frame.pack();

		// Set up the input handler for the player
		inputHandler = new InputHandler(currentLevel.getPlayer(), currentLevel);
    view.addKeyListener(inputHandler);
    view.requestFocusInWindow();

		// Set up the world and start the game
		world = currentLevel;
		PlayerListener playerListener = new PlayerListener(world, view);
		world.addStepListener(playerListener);
		world.start();
	}
	

	public void goToNextLevel() {
		if (currentLevel instanceof Level1) {
				setupLevel(new Level2(this));
				//level now refers to the new level
		} else if (currentLevel instanceof Level2) {
				setupLevel(new Level3(this));		
		}
	}

	public static void main(String[] args) {
		  new Game();			
	}
}