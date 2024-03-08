package menu;

import javax.swing.*;

import audio.AudioHandler;
import input.InputHandler;
import main.GameView;
import main.GameWorld;
import utilities.PlayerListener;

import java.awt.*;
import java.awt.event.ActionListener;

public class Menu extends JPanel {
    private JFrame frame;
    private Image menu;
    private GameWorld world;
    private GameView view;

    public Menu(JFrame frame, int width, int height) {
        this.frame = frame;
        this.setPreferredSize(new Dimension(800, 600));
        addMenuButtons();
        AudioHandler.playMenuSound();
        menu = new ImageIcon("./assets/images/start-screen/title-screen.png").getImage();
    }

    private void addMenuButtons() {
        JButton playButton = createButton("./assets/images/start-screen/play.png", e -> {
            AudioHandler.playButtonSound();
            Timer timer = new Timer(500, evt -> {
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
        world = new GameWorld();
        view = new GameView(world, 800, 600);

        frame.getContentPane().removeAll();
        frame.add(view);
        frame.revalidate();
        frame.repaint();

        InputHandler inputHandler = new InputHandler(world);
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
