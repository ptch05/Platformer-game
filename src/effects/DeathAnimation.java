package effects;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents a temporary death animation that appears on the body of an enemy in the game.
 * The animation is displayed for a brief duration before it is automatically removed. It is used
 * to visually signify the defeat of an enemy, appearing as a small flame.
 * 
 * @author Peiman Timaji, Peiman.Timaji@city.ac.uk
 * @version 1.0
 * @since 1.0
 */

public class DeathAnimation extends DynamicBody {
    private BodyImage deathImage;
    /**
     * The duration for which the death animation should remain visible before being destroyed (in milliseconds).
     */
    private static final long DURATION = 350;

    /**
     * Constructs a death animation at the given position in the world.
     * The animation is initialized with the specified image and is set to be removed after a defined duration.
     *
     * @param world    The game world where the animation is to be displayed.
     * @param position The position in the world where the animation should appear.
     * @param image    The image representing the death animation.
     */

    public DeathAnimation(World world, Vec2 position, BodyImage image) {
        super(world);
        this.deathImage = image;
        this.addImage(deathImage);
        this.setPosition(position);

        // Start a timer to destroy this object after DURATION milliseconds
        Timer timer = new Timer((int) DURATION, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        DeathAnimation.this.destroy();
                    }
                });
            }
        });
        timer.setRepeats(false); // The timer should only run once
        timer.start(); // Start the timer
    }

}

