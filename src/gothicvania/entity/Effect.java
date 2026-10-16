package gothicvania.entity;

import gothicvania.engine.Sprite;
import java.awt.Graphics2D;

/** A one-shot animation that plays once and disappears (death puffs and the like). */
public final class Effect extends Entity {
    private final Sprite sprite;
    private final double frameTime;
    private final boolean faceRight;
    private double time;

    public Effect(Sprite sprite, double frameTime, double anchorX, double anchorY, boolean faceRight) {
        super(anchorX, anchorY, 0, 0);
        this.sprite = sprite;
        this.frameTime = frameTime;
        this.faceRight = faceRight;
    }

    @Override
    public void update(double dt) {
        time += dt;
        if (time >= sprite.duration(frameTime)) removed = true;
    }

    @Override
    public void render(Graphics2D g) {
        sprite.draw(g, sprite.frameAt(time, frameTime, false), x, y, faceRight);
    }

    @Override
    public int layer() {
        return 30;
    }
}
