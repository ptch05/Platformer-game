package gothicvania.entity;

import gothicvania.engine.Assets;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** Scenery standing on the ground: trees, gravestones, bushes, pillars. No collision. */
public final class Prop extends Entity {
    private final BufferedImage image;
    private final boolean foreground;
    private final boolean flip;

    public Prop(String image, double footX, double footY, boolean foreground, boolean flip) {
        super(footX, footY, 0, 0);
        this.image = Assets.image(image);
        this.foreground = foreground;
        this.flip = flip;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void render(Graphics2D g) {
        int w = image.getWidth(), h = image.getHeight();
        int px = (int) Math.round(x - w / 2.0), py = (int) Math.round(y - h + 1);
        if (flip) g.drawImage(image, px + w, py, -w, h, null);
        else g.drawImage(image, px, py, null);
    }

    @Override
    public int layer() {
        return foreground ? 60 : 1;
    }
}
