package gothicvania.engine;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * A set of animation frames cut from a horizontal strip, with mirrored and
 * solid-white ("hit flash") variants prepared up front. Frames are positioned by
 * an anchor point so that differently sized animations line up on the same spot.
 */
public final class Sprite {
    private final BufferedImage[] right;
    private final BufferedImage[] left;
    private final BufferedImage[] flashRight;
    private final BufferedImage[] flashLeft;
    private final int anchorX;
    private final int anchorY;
    private final int width;

    Sprite(BufferedImage strip, int frameWidth, int anchorX, int anchorY, boolean facesRight) {
        int count = Math.max(1, strip.getWidth() / frameWidth);
        width = frameWidth;
        int height = strip.getHeight();
        BufferedImage[] native_ = new BufferedImage[count];
        BufferedImage[] mirrored = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            native_[i] = strip.getSubimage(i * frameWidth, 0, frameWidth, height);
            mirrored[i] = mirror(native_[i]);
        }
        right = facesRight ? native_ : mirrored;
        left = facesRight ? mirrored : native_;
        flashRight = new BufferedImage[count];
        flashLeft = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            flashRight[i] = silhouette(right[i]);
            flashLeft[i] = silhouette(left[i]);
        }
        // The anchor is given for the art as drawn; store it for the right-facing variant.
        this.anchorX = facesRight ? anchorX : frameWidth - anchorX;
        this.anchorY = anchorY;
    }

    public int frames() {
        return right.length;
    }

    /** Draws a frame with its anchor at (x, y). */
    public void draw(Graphics2D g, int index, double x, double y, boolean faceRight) {
        draw(g, index, x, y, faceRight, false, 1f);
    }

    public void draw(Graphics2D g, int index, double x, double y, boolean faceRight, boolean flash, float alpha) {
        BufferedImage[] set = flash ? (faceRight ? flashRight : flashLeft) : (faceRight ? right : left);
        BufferedImage image = set[Math.floorMod(index, set.length)];
        int ax = faceRight ? anchorX : width - anchorX;
        int dx = (int) Math.round(x) - ax;
        int dy = (int) Math.round(y) - anchorY;
        if (alpha >= 1f) {
            g.drawImage(image, dx, dy, null);
        } else if (alpha > 0f) {
            Composite old = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.drawImage(image, dx, dy, null);
            g.setComposite(old);
        }
    }

    /** Frame index for a looping or one-shot animation that has been playing for {@code time} seconds. */
    public int frameAt(double time, double frameDuration, boolean loop) {
        int index = (int) (time / frameDuration);
        return loop ? index % frames() : Math.min(index, frames() - 1);
    }

    public double duration(double frameDuration) {
        return frames() * frameDuration;
    }

    private static BufferedImage mirror(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) out.setRGB(w - 1 - x, y, src.getRGB(x, y));
        }
        return out;
    }

    private static BufferedImage silhouette(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int a = src.getRGB(x, y) >>> 24;
                if (a > 0) out.setRGB(x, y, (a << 24) | 0xFFFFFF);
            }
        }
        return out;
    }
}
