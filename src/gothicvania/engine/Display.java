package gothicvania.engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * The game window. Everything is drawn into a small fixed-size back buffer
 * ({@link #WIDTH}x{@link #HEIGHT}, the native resolution of the pixel art) which
 * is then scaled up to fill the window with nearest-neighbour filtering.
 * Updates run at a fixed 60Hz so physics behaves identically on every machine.
 */
public final class Display {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 225;
    public static final double STEP = 1.0 / 60.0;

    /** Something the display can drive: a fixed-step update and a render into the back buffer. */
    public interface Loop {
        void update(double dt);
        void render(Graphics2D g);
    }

    private final JFrame frame;
    private final Canvas canvas;
    private final Input input;
    private final BufferedImage screen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private volatile boolean running = true;
    private boolean fullscreen;

    public Display(String title, Input input) {
        this.input = input;
        canvas = new Canvas();
        canvas.setBackground(Color.BLACK);
        canvas.setIgnoreRepaint(true);
        canvas.setFocusTraversalKeysEnabled(false);
        canvas.addKeyListener(input);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int scale = Math.max(1, Math.min(4, Math.min((screenSize.width - 80) / WIDTH, (screenSize.height - 120) / HEIGHT)));
        canvas.setPreferredSize(new Dimension(WIDTH * scale, HEIGHT * scale));

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                stop();
            }
        });
        frame.add(canvas);
        frame.pack();
        frame.setMinimumSize(new Dimension(WIDTH + 40, HEIGHT + 60));
        frame.setLocationRelativeTo(null);
    }

    /** Opens the window and runs the loop on a dedicated thread until the window closes or {@link #stop()} is called. */
    public void start(Loop loop) {
        frame.setVisible(true);
        canvas.createBufferStrategy(2);
        canvas.requestFocus();
        Thread thread = new Thread(() -> run(loop), "game-loop");
        thread.start();
    }

    public void stop() {
        running = false;
    }

    public void toggleFullscreen() {
        SwingUtilities.invokeLater(() -> {
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            fullscreen = !fullscreen;
            device.setFullScreenWindow(fullscreen ? frame : null);
            canvas.requestFocus();
        });
    }

    private void run(Loop loop) {
        long last = System.nanoTime();
        double accumulator = 0;
        while (running) {
            long now = System.nanoTime();
            accumulator += Math.min((now - last) / 1e9, 0.25);
            last = now;
            boolean updated = false;
            while (accumulator >= STEP) {
                input.poll();
                loop.update(STEP);
                accumulator -= STEP;
                updated = true;
            }
            if (updated) {
                present(loop);
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        SwingUtilities.invokeLater(frame::dispose);
        Audio.get().shutdown();
        System.exit(0);
    }

    private void present(Loop loop) {
        Graphics2D g = screen.createGraphics();
        loop.render(g);
        g.dispose();

        BufferStrategy strategy = canvas.getBufferStrategy();
        if (strategy == null) return;
        do {
            do {
                Graphics2D out = (Graphics2D) strategy.getDrawGraphics();
                int cw = canvas.getWidth(), ch = canvas.getHeight();
                double scale = Math.min(cw / (double) WIDTH, ch / (double) HEIGHT);
                if (scale >= 1) scale = Math.floor(scale * 4) / 4; // quarter steps stay crisp on hi-dpi screens
                int w = (int) (WIDTH * scale), h = (int) (HEIGHT * scale);
                out.setColor(Color.BLACK);
                out.fillRect(0, 0, cw, ch);
                out.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                out.drawImage(screen, (cw - w) / 2, (ch - h) / 2, w, h, null);
                out.dispose();
            } while (strategy.contentsRestored());
            strategy.show();
        } while (strategy.contentsLost());
        Toolkit.getDefaultToolkit().sync();
    }

    /** Renders one frame off-screen (used by the screenshot tool). */
    public static BufferedImage renderOffscreen(Loop loop) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        loop.render(g);
        g.dispose();
        return image;
    }
}
