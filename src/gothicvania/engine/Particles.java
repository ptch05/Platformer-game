package gothicvania.engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A fixed-size pool of square particles stored as parallel arrays (no allocation
 * per particle). Used for dust, sparks, embers, blood, souls and ambient weather.
 */
public final class Particles {
    private static final int MAX = 2400;

    private final float[] x = new float[MAX], y = new float[MAX];
    private final float[] vx = new float[MAX], vy = new float[MAX];
    private final float[] life = new float[MAX], maxLife = new float[MAX];
    private final float[] gravity = new float[MAX], drag = new float[MAX];
    private final float[] size = new float[MAX];
    private final int[] rgb = new int[MAX];
    private final boolean[] shrink = new boolean[MAX];
    private int count;

    /** Fluent description of a burst; reuse one instance per call site style. */
    public static final class Burst {
        float speedMin = 20, speedMax = 60, angle = -90, spread = 360;
        float lifeMin = 0.3f, lifeMax = 0.7f, gravity = 0, drag = 0, sizeMin = 1, sizeMax = 2;
        int[] colors = {0xFFFFFF};
        boolean shrink = true;
        float jitterX, jitterY;

        public Burst speed(double min, double max) { speedMin = (float) min; speedMax = (float) max; return this; }
        /** Direction in degrees (0 = right, -90 = up) and total spread. */
        public Burst direction(double degrees, double spreadDegrees) { angle = (float) degrees; spread = (float) spreadDegrees; return this; }
        public Burst life(double min, double max) { lifeMin = (float) min; lifeMax = (float) max; return this; }
        public Burst gravity(double g) { gravity = (float) g; return this; }
        public Burst drag(double d) { drag = (float) d; return this; }
        public Burst size(double min, double max) { sizeMin = (float) min; sizeMax = (float) max; return this; }
        public Burst colors(int... c) { colors = c; return this; }
        public Burst fade() { shrink = false; return this; }
        public Burst area(double w, double h) { jitterX = (float) w; jitterY = (float) h; return this; }
    }

    public static Burst burst() {
        return new Burst();
    }

    public void emit(double px, double py, int amount, Burst b) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int n = 0; n < amount; n++) {
            if (count >= MAX) return;
            int i = count++;
            double a = Math.toRadians(b.angle + (r.nextDouble() - 0.5) * b.spread);
            double s = b.speedMin + r.nextDouble() * (b.speedMax - b.speedMin);
            x[i] = (float) (px + (r.nextDouble() - 0.5) * b.jitterX);
            y[i] = (float) (py + (r.nextDouble() - 0.5) * b.jitterY);
            vx[i] = (float) (Math.cos(a) * s);
            vy[i] = (float) (Math.sin(a) * s);
            maxLife[i] = life[i] = b.lifeMin + r.nextFloat() * (b.lifeMax - b.lifeMin);
            gravity[i] = b.gravity;
            drag[i] = b.drag;
            size[i] = b.sizeMin + r.nextFloat() * (b.sizeMax - b.sizeMin);
            rgb[i] = b.colors[r.nextInt(b.colors.length)];
            shrink[i] = b.shrink;
        }
    }

    public void update(double dt) {
        float t = (float) dt;
        for (int i = 0; i < count; i++) {
            life[i] -= t;
            if (life[i] <= 0) {
                swapRemove(i--);
                continue;
            }
            vy[i] += gravity[i] * t;
            float k = 1 - drag[i] * t;
            vx[i] *= k;
            vy[i] *= k;
            x[i] += vx[i] * t;
            y[i] += vy[i] * t;
        }
    }

    public void render(Graphics2D g, int camX, int camY) {
        for (int i = 0; i < count; i++) {
            float f = life[i] / maxLife[i];
            float s = shrink[i] ? Math.max(1, size[i] * f) : size[i];
            int px = (int) (x[i] - camX - s / 2), py = (int) (y[i] - camY - s / 2);
            if (px < -8 || py < -8 || px > Display.WIDTH + 8 || py > Display.HEIGHT + 8) continue;
            int alpha = shrink[i] ? 255 : (int) (255 * Math.min(1, f * 2));
            g.setColor(new Color((alpha << 24) | rgb[i], true));
            int is = Math.max(1, Math.round(s));
            g.fillRect(px, py, is, is);
        }
    }

    public void clear() {
        count = 0;
    }

    private void swapRemove(int i) {
        int last = --count;
        x[i] = x[last]; y[i] = y[last]; vx[i] = vx[last]; vy[i] = vy[last];
        life[i] = life[last]; maxLife[i] = maxLife[last]; gravity[i] = gravity[last]; drag[i] = drag[last];
        size[i] = size[last]; rgb[i] = rgb[last]; shrink[i] = shrink[last];
    }
}
