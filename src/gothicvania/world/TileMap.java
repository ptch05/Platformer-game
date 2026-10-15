package gothicvania.world;

import gothicvania.engine.Assets;
import gothicvania.engine.Display;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * The level's grid of tiles: collision queries plus rendering. Static tiles are
 * baked into 256px chunk images on first sight (and re-baked when a breakable
 * wall crumbles); lava is animated so it is drawn every frame.
 */
public final class TileMap {
    public static final int SIZE = 16;
    private static final int CHUNK_TILES = 16;
    private static final int LAVA_FRAMES = 6;
    private static final int LAVA_FRAME_W = 128;
    private static final Color EDGE = new Color(6, 3, 14, 170);
    private static final Color LAVA_DEEP = new Color(169, 59, 59);

    public final int cols;
    public final int rows;
    private final Tile[] tiles;
    private final byte[] breakHits;
    private final Theme theme;
    private final BufferedImage tileSheet;
    private final BufferedImage spikes;
    private final BufferedImage lava;
    private final BufferedImage[][] chunks;

    public TileMap(int cols, int rows, Theme theme) {
        this.cols = cols;
        this.rows = rows;
        this.theme = theme;
        tiles = new Tile[cols * rows];
        Arrays.fill(tiles, Tile.EMPTY);
        breakHits = new byte[cols * rows];
        tileSheet = Assets.image(theme.tiles());
        spikes = Assets.image(theme.spikes());
        lava = Assets.image("sprites/fx/lava.png");
        chunks = new BufferedImage[(cols + CHUNK_TILES - 1) / CHUNK_TILES][(rows + CHUNK_TILES - 1) / CHUNK_TILES];
    }

    public int widthPx() {
        return cols * SIZE;
    }

    public int heightPx() {
        return rows * SIZE;
    }

    // ------------------------------------------------------------------ queries

    /** Outside the map the sides are walls and the top and bottom are open. */
    public Tile get(int tx, int ty) {
        if (tx < 0 || tx >= cols) return Tile.SOLID;
        if (ty < 0 || ty >= rows) return Tile.EMPTY;
        return tiles[ty * cols + tx];
    }

    public void set(int tx, int ty, Tile tile) {
        if (tx < 0 || ty < 0 || tx >= cols || ty >= rows) return;
        tiles[ty * cols + tx] = tile;
        invalidate(tx, ty);
    }

    public boolean solid(int tx, int ty) {
        return get(tx, ty).solid();
    }

    /** True if any solid tile overlaps the rectangle. */
    public boolean solidIn(double x, double y, double w, double h) {
        int x0 = tile(x), x1 = tile(x + w - 0.001), y0 = tile(y), y1 = tile(y + h - 0.001);
        for (int ty = y0; ty <= y1; ty++) {
            for (int tx = x0; tx <= x1; tx++) {
                if (solid(tx, ty)) return true;
            }
        }
        return false;
    }

    /** The hazard (spikes or lava) the rectangle touches, or null. Only the dangerous part of each tile counts. */
    public Tile hazardIn(double x, double y, double w, double h) {
        int x0 = tile(x + 2), x1 = tile(x + w - 2), y0 = tile(y), y1 = tile(y + h - 0.001);
        for (int ty = y0; ty <= y1; ty++) {
            for (int tx = x0; tx <= x1; tx++) {
                Tile t = get(tx, ty);
                if (!t.hazard()) continue;
                double top = ty * SIZE + (t == Tile.SPIKES ? 8 : (get(tx, ty - 1) == Tile.LAVA ? 0 : 6));
                if (y + h > top) return t;
            }
        }
        return null;
    }

    /** Whether there's a hazard within {@code range} tiles left or right of a tile on the same row or just below. */
    public boolean hazardNear(int tx, int ty, int range) {
        for (int dx = -range; dx <= range; dx++) {
            if (get(tx + dx, ty).hazard() || get(tx + dx, ty + 1).hazard()) return true;
        }
        return false;
    }

    /** Hits a breakable wall; returns true if it broke. */
    public boolean strikeBreakable(int tx, int ty) {
        if (get(tx, ty) != Tile.BREAKABLE) return false;
        int i = ty * cols + tx;
        if (++breakHits[i] >= 2) {
            set(tx, ty, Tile.EMPTY);
            return true;
        }
        invalidate(tx, ty);
        return false;
    }

    public static int tile(double px) {
        return Math.floorDiv((int) Math.floor(px), SIZE);
    }

    // ------------------------------------------------------------------ rendering

    public void render(Graphics2D g, int camX, int camY, double time) {
        int chunkPx = CHUNK_TILES * SIZE;
        int cx0 = Math.max(0, Math.floorDiv(camX, chunkPx)), cx1 = Math.min(chunks.length - 1, Math.floorDiv(camX + Display.WIDTH, chunkPx));
        int cy0 = Math.max(0, Math.floorDiv(camY, chunkPx)), cy1 = Math.min(chunks[0].length - 1, Math.floorDiv(camY + Display.HEIGHT, chunkPx));
        for (int cx = cx0; cx <= cx1; cx++) {
            for (int cy = cy0; cy <= cy1; cy++) {
                if (chunks[cx][cy] == null) chunks[cx][cy] = bake(cx, cy);
                g.drawImage(chunks[cx][cy], cx * chunkPx - camX, cy * chunkPx - camY, null);
            }
        }
        renderLava(g, camX, camY, time);
    }

    /** Draws every tile (for the map overview tool). */
    public void renderAll(Graphics2D g) {
        int chunkPx = CHUNK_TILES * SIZE;
        for (int cx = 0; cx < chunks.length; cx++) {
            for (int cy = 0; cy < chunks[0].length; cy++) {
                if (chunks[cx][cy] == null) chunks[cx][cy] = bake(cx, cy);
                g.drawImage(chunks[cx][cy], cx * chunkPx, cy * chunkPx, null);
            }
        }
        drawLava(g, 0, 0, 0, 0, cols - 1, 0, rows - 1);
    }

    private void renderLava(Graphics2D g, int camX, int camY, double time) {
        int tx0 = Math.max(0, tile(camX)), tx1 = Math.min(cols - 1, tile(camX + Display.WIDTH));
        int ty0 = Math.max(0, tile(camY)), ty1 = Math.min(rows - 1, tile(camY + Display.HEIGHT));
        drawLava(g, camX, camY, (int) (time / 0.17) % LAVA_FRAMES, tx0, tx1, ty0, ty1);
    }

    private void drawLava(Graphics2D g, int camX, int camY, int frame, int tx0, int tx1, int ty0, int ty1) {
        for (int ty = ty0; ty <= ty1; ty++) {
            for (int tx = tx0; tx <= tx1; tx++) {
                if (get(tx, ty) != Tile.LAVA) continue;
                int depth = 0;
                while (depth < 3 && get(tx, ty - depth - 1) == Tile.LAVA) depth++;
                int dx = tx * SIZE - camX, dy = ty * SIZE - camY;
                if (depth >= 3) {
                    g.setColor(LAVA_DEEP);
                    g.fillRect(dx, dy, SIZE, SIZE);
                } else {
                    int sx = frame * LAVA_FRAME_W + Math.floorMod(tx * SIZE, LAVA_FRAME_W);
                    int sy = depth * SIZE;
                    g.drawImage(lava, dx, dy, dx + SIZE, dy + SIZE, sx, sy, sx + SIZE, sy + SIZE, null);
                }
            }
        }
    }

    private void invalidate(int tx, int ty) {
        // neighbours' auto-tiling depends on this tile, so refresh the chunk(s) around it
        for (int dy = -1; dy <= 3; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int cx = Math.floorDiv(tx + dx, CHUNK_TILES), cy = Math.floorDiv(ty + dy, CHUNK_TILES);
                if (cx >= 0 && cy >= 0 && cx < chunks.length && cy < chunks[0].length) chunks[cx][cy] = null;
            }
        }
    }

    private BufferedImage bake(int cx, int cy) {
        int px = CHUNK_TILES * SIZE;
        BufferedImage img = new BufferedImage(px, px, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        for (int ty = cy * CHUNK_TILES; ty < Math.min(rows, (cy + 1) * CHUNK_TILES); ty++) {
            for (int tx = cx * CHUNK_TILES; tx < Math.min(cols, (cx + 1) * CHUNK_TILES); tx++) {
                drawStaticTile(g, tx, ty, (tx - cx * CHUNK_TILES) * SIZE, (ty - cy * CHUNK_TILES) * SIZE);
            }
        }
        g.dispose();
        return img;
    }

    private void drawStaticTile(Graphics2D g, int tx, int ty, int x, int y) {
        Tile t = get(tx, ty);
        int variant = hash(tx, ty) & 3;
        switch (t) {
            case SOLID, BREAKABLE -> {
                int depth = 0;
                while (depth < 2 && solidForArt(tx, ty - depth - 1)) depth++;
                drawSheetTile(g, variant, depth, x, y);
                if (!solidForArt(tx - 1, ty)) shade(g, x, y, 1, SIZE);
                if (!solidForArt(tx + 1, ty)) shade(g, x + SIZE - 1, y, 1, SIZE);
                if (!solidForArt(tx, ty + 1) && ty + 1 < rows) shade(g, x, y + SIZE - 2, SIZE, 2);
                if (t == Tile.BREAKABLE) drawCracks(g, x, y, breakHits[ty * cols + tx]);
            }
            case PLATFORM -> {
                g.drawImage(tileSheet, x, y, x + SIZE, y + 6, variant * SIZE, 0, variant * SIZE + SIZE, 6, null);
                g.setColor(EDGE);
                g.fillRect(x, y + 6, SIZE, 1);
                g.setColor(new Color(0, 0, 0, 70));
                g.fillRect(x + 3, y + 7, 2, 3);
                g.fillRect(x + 11, y + 7, 2, 2);
            }
            case SPIKES -> {
                int half = (tx & 1) * 16;
                g.drawImage(spikes, x, y, x + SIZE, y + SIZE, half, 0, half + 16, 16, null);
            }
            case GATE -> drawGate(g, x, y, get(tx, ty - 1) != Tile.GATE);
            default -> {
            }
        }
    }

    /** Draws rock over an undiscovered secret cell (world coordinates). */
    public void drawCover(Graphics2D g, int tx, int ty) {
        drawSheetTile(g, hash(tx, ty) & 3, 2, tx * SIZE, ty * SIZE);
    }

    /** Breakable walls count as ground for art purposes so secrets aren't obvious. */
    private boolean solidForArt(int tx, int ty) {
        Tile t = get(tx, ty);
        return t == Tile.SOLID || t == Tile.BREAKABLE;
    }

    private void drawSheetTile(Graphics2D g, int col, int row, int x, int y) {
        int sx = col * SIZE, sy = row * SIZE;
        g.drawImage(tileSheet, x, y, x + SIZE, y + SIZE, sx, sy, sx + SIZE, sy + SIZE, null);
    }

    private static void shade(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(EDGE);
        g.fillRect(x, y, w, h);
    }

    private static void drawCracks(Graphics2D g, int x, int y, int hits) {
        g.setColor(new Color(0, 0, 0, hits > 0 ? 200 : 110));
        int[][] lines = {{3, 2, 6, 6}, {6, 6, 5, 10}, {6, 6, 11, 8}, {11, 8, 13, 13}, {5, 10, 8, 14}};
        for (int[] l : lines) g.drawLine(x + l[0], y + l[1], x + l[2], y + l[3]);
        if (hits > 0) {
            g.drawLine(x + 9, y + 2, x + 11, y + 8);
            g.drawLine(x + 2, y + 9, x + 5, y + 10);
        }
    }

    private static void drawGate(Graphics2D g, int x, int y, boolean top) {
        for (int i = 0; i < 4; i++) {
            int bx = x + 1 + i * 4;
            g.setColor(new Color(28, 24, 34));
            g.fillRect(bx, y, 3, SIZE);
            g.setColor(new Color(84, 76, 96));
            g.fillRect(bx, y, 1, SIZE);
            if (top) {
                g.setColor(new Color(120, 110, 130));
                g.fillRect(bx + 1, y - 1, 1, 2);
            }
        }
        g.setColor(new Color(40, 34, 46));
        g.fillRect(x, y + 5, SIZE, 2);
    }

    private static int hash(int x, int y) {
        int h = x * 374761393 + y * 668265263;
        h = (h ^ (h >>> 13)) * 1274126177;
        return h ^ (h >>> 16);
    }
}
