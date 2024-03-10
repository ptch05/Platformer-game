package main;

import javax.swing.JFrame;

import menu.Menu;

public class Game {
	private JFrame frame;

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
			
			Menu menu = new Menu(frame, 800, 600);
			frame.add(menu);
			frame.pack();
			 // finally, make the frame visible
			 frame.setVisible(true);
			 
	}

	public static void main(String[] args) {
		  new Game();			
	}
}