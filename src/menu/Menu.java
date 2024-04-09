package menu;

import javax.swing.*;

import audio.AudioHandler;
import main.Game;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Represents the main menu of the game, displaying options to start the game or quit.
 * It includes buttons for user interaction and plays menu music upon loading.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */
public class Menu extends JPanel {
    private JFrame frame;
    private Image menu;
    private Game game;

    public Menu(JFrame frame, int width, int height, Game game) {
        this.frame = frame;
        this.game = game;
        this.setPreferredSize(new Dimension(800, 600));
        addMenuButtons();
        AudioHandler.playMenuSound();
        menu = new ImageIcon("./assets/images/start-screen/title-screen.png").getImage();
    }

    /**
     * Adds menu buttons like "Play" and "Quit" to the panel. Buttons have customized graphics
     * and are configured with action listeners for user interactions.
     */
    private void addMenuButtons() {
        JButton playButton = createButton("./assets/images/start-screen/play.png", e -> {
            AudioHandler.playButtonSound();
            Timer timer = new Timer(500, evt -> { //Added a short delay between clicking the button and actually loading into the game, to make it feel a little more game-like.
                game.startGame();
                AudioHandler.stopMenuSound();
            });
            timer.setRepeats(false);
            timer.start();
        });

        JButton quitButton = createButton("./assets/images/start-screen/quit.png", e -> {
            AudioHandler.playButtonSound();
            Timer timer = new Timer(500, evt -> System.exit(0));
            timer.start();
        });

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalGlue());
        add(playButton);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(quitButton);
        add(Box.createRigidArea(new Dimension(0, 80)));
    }

    /**
     * Creates a button with a specified image and action listener. This utility method is used
     * to create visually styled buttons for the menu.
     * 
     * @param imagePath The path to the image used as the button's icon.
     * @param listener The action listener attached to the button to handle user actions.
     * @return A new JButton instance styled according to the specified image and configured with the provided listener.
     */
    private JButton createButton(String imagePath, ActionListener listener) {
        ImageIcon icon = new ImageIcon(imagePath);
        JButton button = new JButton(icon);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.addActionListener(listener);
        return button;
    }

    /**
     * Overridden paintComponent method to draw the menu's background image. Ensures the image
     * covers the entire panel area.
     * 
     * @param g The {@code Graphics} object used for drawing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(menu, 0, 0, getWidth(), getHeight(), this);
    }
}
