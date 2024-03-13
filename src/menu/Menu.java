package menu;

import javax.swing.*;

import audio.AudioHandler;
import input.InputHandler;
import levels.GameLevel;
import levels.Level1;
import main.Game;
import main.GameView;
import utilities.PlayerListener;
import city.cs.engine.*;
import entities.Player;

import java.awt.*;
import java.awt.event.ActionListener;

public class Menu extends JPanel {
    private JFrame frame;
    private Image menu;
    private World world;
    private GameView view;
    private GameLevel gameLevel;
    private Game game;
    private Player player;
    GameLevel currentLevel;

    public Menu(JFrame frame, int width, int height, GameLevel gameLevel) {
        this.frame = frame;
        this.setPreferredSize(new Dimension(800, 600));
        addMenuButtons();
        AudioHandler.playMenuSound();
        menu = new ImageIcon("./assets/images/start-screen/title-screen.png").getImage();
    }

    private void addMenuButtons() {
        JButton playButton = createButton("./assets/images/start-screen/play.png", e -> {
            AudioHandler.playButtonSound();
            Timer timer = new Timer(500, evt -> { //I added these 2 timers so that there's a little bit of a delay between clicking the button and actually loading into the game, to make it feel a little more game-like.
                startGame();
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

    private void startGame() { //DO NOT TOUCH THIS
        currentLevel = new Level1(game);
        view = new GameView(gameLevel, 800, 600);

        frame.getContentPane().removeAll();
        frame.add(view);
        frame.revalidate();
        frame.repaint();

        InputHandler inputHandler = new InputHandler(player);
        view.addKeyListener(inputHandler);
        view.requestFocusInWindow();

        PlayerListener playerListener = new PlayerListener(world, view);
        world.addStepListener(playerListener);

        world.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(menu, 0, 0, getWidth(), getHeight(), this);
    }
}
