package effects;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeathAnimation extends DynamicBody {
    private BodyImage deathImage;
    private static final long DURATION = 350;

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

