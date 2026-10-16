package gothicvania.engine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Darkness with holes cut out for light sources. Each frame the overlay is filled
 * with the ambient darkness, lights are erased out of it with soft radial falloff,
 * then tinted glows are added on top before the overlay is drawn over the scene.
 */
public final class Lighting {
    private static final int GRADIENT_SIZE = 128;

    private final BufferedImage overlay = new BufferedImage(Display.WIDTH, Display.HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private final BufferedImage falloff = radial(new Color(255, 255, 255, 255));
    private final Map<Integer, BufferedImage> glows = new HashMap<>();
    private Graphics2D g;
    private Color ambient = new Color(0, 0, 0, 0);
    private final List<float[]> tints = new ArrayList<>();

    /** Starts a frame. {@code darkness} is the overlay colour (alpha = how dark unlit areas are). */
    public void begin(Color darkness) {
        ambient = darkness;
        tints.clear();
        g = overlay.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setColor(darkness);
        g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);
        g.setComposite(AlphaComposite.DstOut);
    }

    /**
     * Cuts a light at a screen position.
     *
     * @param radius    radius in pixels
     * @param intensity 0..1, how much of the darkness is removed at the centre
     * @param tint      optional colour glow added on top (0xRRGGBB) or -1 for none
     */
    public void light(double sx, double sy, double radius, float intensity, int tint) {
        if (g == null || sx + radius < 0 || sy + radius < 0 || sx - radius > Display.WIDTH || sy - radius > Display.HEIGHT) return;
        int d = (int) (radius * 2);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, Math.min(1f, intensity)));
        g.drawImage(falloff, (int) (sx - radius), (int) (sy - radius), d, d, null);
        if (tint >= 0) tints.add(new float[]{(float) sx, (float) sy, (float) radius, intensity, tint});
    }

    /** Finishes the frame and composites it over the scene. */
    public void end(Graphics2D target) {
        if (g == null) return;
        g.dispose();
        g = null;
        if (ambient.getAlpha() > 0) target.drawImage(overlay, 0, 0, null);
        for (float[] t : tints) {
            BufferedImage glow = glows.computeIfAbsent((int) t[4], c -> radial(new Color(c)));
            int r = (int) (t[2] * 0.8f), d = r * 2;
            target.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, t[3] * 0.32f)));
            target.drawImage(glow, (int) (t[0] - r), (int) (t[1] - r), d, d, null);
        }
        target.setComposite(AlphaComposite.SrcOver);
    }

    private static BufferedImage radial(Color color) {
        BufferedImage img = new BufferedImage(GRADIENT_SIZE, GRADIENT_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gg = img.createGraphics();
        float c = GRADIENT_SIZE / 2f;
        Color clear = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
        gg.setPaint(new RadialGradientPaint(new Point2D.Float(c, c), c, new float[]{0f, 0.35f, 1f},
            new Color[]{color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 150), clear},
            MultipleGradientPaint.CycleMethod.NO_CYCLE));
        gg.fillRect(0, 0, GRADIENT_SIZE, GRADIENT_SIZE);
        gg.dispose();
        return img;
    }
}
