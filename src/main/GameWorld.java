package main;


import city.cs.engine.*;
import java.awt.*;

import city.cs.engine.Shape;
import entities.Player;
import entities.Skeleton;

import org.jbox2d.common.Vec2;

public class GameWorld extends World {
    // Constructor and methods for world management
    private Player player;
    private Skeleton skeleton;

    public GameWorld() {
        // Initialize your world here
        player = new Player(this);
        player.setPosition(new Vec2(0,0));
        skeleton = new Skeleton(this);
        skeleton.setPosition(new Vec2(0,0));
    }

    public Player getPlayer(){
        return player;
    }

}