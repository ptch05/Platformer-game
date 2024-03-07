package main;

import java.awt.Dimension;

import javax.swing.JFrame;

import menu.Menu;

public class Game {
	private JFrame frame;

	public Game() {
			frame = new JFrame("GothicVania Cemetery");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationByPlatform(true);
			frame.setResizable(true);
			frame.setSize(new Dimension(800, 600));

			Menu menu = new Menu(frame, 800, 600);
			frame.add(menu);
			frame.pack();
			frame.setVisible(true);
	}

	public static void main(String[] args) {
		  new Game();
	}
}