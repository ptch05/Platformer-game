package main;

import city.cs.engine.*;
import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame(UserView view) {
        setTitle("Cyber Reckoning");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        add(view);
        pack(); 
        setVisible(true);
    }
}