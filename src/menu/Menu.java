package menu;

import javax.swing.*;
import input.InputHandler;
import main.GameView;
import main.GameWorld;
import utilities.PlayerListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JPanel {
    private JFrame frame;
    private Image menu;
    private GameWorld world;
    private GameView view;

    public Menu(JFrame frame, int width, int height) {
        this.frame = frame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addMenuButtons();
    }

    private void addMenuButtons() {
        // Play button
       JButton playButton = new JButton("Play");
       //playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        // Quit button
       JButton quitButton = new JButton("Quit");
       //quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(Box.createVerticalGlue());
        add(playButton);
        add(Box.createRigidArea(new Dimension(800, 600)));
        add(quitButton);
        add(Box.createVerticalGlue());
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
        menu = new ImageIcon("./assets/images/misc/title-screen.png").getImage();
        g.drawImage(menu, 0, 0, getWidth(), getHeight(), this);
    }
}
