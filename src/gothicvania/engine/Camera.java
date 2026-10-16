package gothicvania.engine;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A smoothed follow camera with look-ahead, bounds clamping and trauma-based shake.
 * Coordinates are world pixels; {@link #x()} and {@link #y()} give the top-left of
 * the view, rounded so pixel art never shimmers.
 */
public final class Camera {
    private double x, y;
    private double minX, minY, maxX, maxY;
    private double trauma;
    private double shakeX, shakeY;
    private double lookAhead;

    public void setBounds(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = Math.max(minX, maxX - Display.WIDTH);
        this.maxY = Math.max(minY, maxY - Display.HEIGHT);
    }

    /** Jumps straight to a target (used on spawn and respawn). */
    public void snap(double targetX, double targetY) {
        x = clamp(targetX - Display.WIDTH / 2.0, minX, maxX);
        y = clamp(targetY - Display.HEIGHT / 2.0, minY, maxY);
    }

    /**
     * Eases toward a focus point. The camera leads in the direction of travel and
     * sits a little below centre so the player can see what's ahead and above.
     */
    public void follow(double focusX, double focusY, int facing, double dt) {
        lookAhead = approach(lookAhead, facing * 34, 90 * dt);
        double tx = focusX + lookAhead - Display.WIDTH / 2.0;
        double ty = focusY - Display.HEIGHT * 0.56;
        double k = 1 - Math.exp(-dt * 6.5);
        double ky = 1 - Math.exp(-dt * 4.5);
        x += (clamp(tx, minX, maxX) - x) * k;
        y += (clamp(ty, minY, maxY) - y) * ky;
        x = clamp(x, minX, maxX);
        y = clamp(y, minY, maxY);

        trauma = Math.max(0, trauma - dt * 1.6);
        double shake = trauma * trauma;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        shakeX = (r.nextDouble() * 2 - 1) * 9 * shake;
        shakeY = (r.nextDouble() * 2 - 1) * 7 * shake;
    }

    /** Adds screen shake; 0.2 is a bump, 0.6 a big hit, 1.0 an explosion. */
    public void shake(double amount) {
        trauma = Math.min(1, trauma + amount);
    }

    public int x() {
        return (int) Math.round(x + shakeX);
    }

    public int y() {
        return (int) Math.round(y + shakeY);
    }

    public double rawX() {
        return x;
    }

    public double rawY() {
        return y;
    }

    public boolean isVisible(double left, double top, double width, double height, double margin) {
        return left + width > x - margin && left < x + Display.WIDTH + margin
            && top + height > y - margin && top < y + Display.HEIGHT + margin;
    }

    private static double clamp(double v, double lo, double hi) {
        return v < lo ? lo : v > hi ? hi : v;
    }

    private static double approach(double v, double target, double amount) {
        return v < target ? Math.min(target, v + amount) : Math.max(target, v - amount);
    }
}
