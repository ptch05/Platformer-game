package gothicvania.entity;

import gothicvania.engine.Lighting;
import gothicvania.world.Level;
import gothicvania.world.Tile;
import gothicvania.world.TileMap;
import java.awt.Graphics2D;

/**
 * Anything that lives in a level. Position is the top-left of an axis-aligned
 * hitbox in world pixels. {@link #move(double)} resolves collisions against the
 * tile map and one-way platform entities one axis at a time.
 */
public abstract class Entity {
    public double x, y, w, h;
    public double vx, vy;
    public int facing = 1;
    public boolean onGround;
    public boolean removed;
    /** Set when the last horizontal move was blocked: -1 left, 1 right, 0 free. */
    public int wallContact;
    public boolean hitCeiling;
    public Platform riding;
    protected Level level;

    protected Entity(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void attach(Level level) {
        this.level = level;
    }

    public abstract void update(double dt);

    public abstract void render(Graphics2D g);

    /** Drawn after the lighting pass (glows, text). */
    public void renderOverlay(Graphics2D g) {
    }

    /** Called by the lighting pass so the entity can cut light into the darkness. */
    public void emitLight(Lighting lighting, int camX, int camY) {
    }

    /** Lower numbers draw first. */
    public int layer() {
        return 10;
    }

    public double cx() {
        return x + w / 2;
    }

    public double cy() {
        return y + h / 2;
    }

    public double bottom() {
        return y + h;
    }

    public double right() {
        return x + w;
    }

    public boolean overlaps(Entity o) {
        return overlaps(o.x, o.y, o.w, o.h);
    }

    public boolean overlaps(double ox, double oy, double ow, double oh) {
        return x < ox + ow && x + w > ox && y < oy + oh && y + h > oy;
    }

    public double distanceTo(Entity o) {
        return Math.hypot(o.cx() - cx(), o.cy() - cy());
    }

    // ------------------------------------------------------------------ physics

    /** Moves by velocity, stopping at walls, floors, ceilings and one-way platforms. */
    protected void move(double dt) {
        moveX(vx * dt);
        moveY(vy * dt);
    }

    protected void moveX(double dx) {
        wallContact = 0;
        if (dx == 0) return;
        TileMap map = level.map;
        double nx = x + dx;
        int top = TileMap.tile(y + 1), bottom = TileMap.tile(y + h - 1);
        if (dx > 0) {
            int col = TileMap.tile(nx + w - 0.001);
            for (int row = top; row <= bottom; row++) {
                if (map.solid(col, row)) {
                    nx = col * TileMap.SIZE - w;
                    wallContact = 1;
                    vx = 0;
                    break;
                }
            }
        } else {
            int col = TileMap.tile(nx);
            for (int row = top; row <= bottom; row++) {
                if (map.solid(col, row)) {
                    nx = (col + 1) * TileMap.SIZE;
                    wallContact = -1;
                    vx = 0;
                    break;
                }
            }
        }
        x = nx;
    }

    protected void moveY(double dy) {
        TileMap map = level.map;
        double oldBottom = y + h;
        double ny = y + dy;
        onGround = false;
        hitCeiling = false;
        riding = null;
        int left = TileMap.tile(x + 1), right = TileMap.tile(x + w - 1);
        if (dy > 0) {
            int row = TileMap.tile(ny + h - 0.001);
            int oldRow = TileMap.tile(oldBottom - 0.001);
            for (int col = left; col <= right; col++) {
                Tile t = map.get(col, row);
                boolean landsOnLedge = t == Tile.PLATFORM && oldRow < row && !droppingThrough();
                if (t.solid() || landsOnLedge) {
                    ny = row * TileMap.SIZE - h;
                    onGround = true;
                    vy = 0;
                    break;
                }
            }
            if (!onGround && !droppingThrough()) {
                for (Platform p : level.platforms()) {
                    if (!p.isSolidTop() || x + w <= p.x + 1 || x >= p.x + p.w - 1) continue;
                    if (oldBottom <= p.y + 1.5 && ny + h >= p.y) {
                        ny = p.y - h;
                        onGround = true;
                        riding = p;
                        vy = 0;
                        break;
                    }
                }
            }
        } else if (dy < 0) {
            int row = TileMap.tile(ny);
            for (int col = left; col <= right; col++) {
                if (map.solid(col, row)) {
                    ny = (row + 1) * TileMap.SIZE;
                    hitCeiling = true;
                    vy = 0;
                    break;
                }
            }
        }
        y = ny;
        if (dy == 0) onGround = groundBelow();
    }

    /** Shifts the entity with a platform it stands on, still respecting walls. */
    void carry(double dx, double dy) {
        double keep = vx;
        int keepWall = wallContact;
        moveX(dx);
        vx = keep;
        wallContact = keepWall;
        y += dy;
    }

    /** Whether something supports the entity right now (used when it isn't moving vertically). */
    protected boolean groundBelow() {
        TileMap map = level.map;
        int row = TileMap.tile(y + h + 0.5);
        int left = TileMap.tile(x + 1), right = TileMap.tile(x + w - 1);
        for (int col = left; col <= right; col++) {
            Tile t = map.get(col, row);
            if (t.solid() || (t == Tile.PLATFORM && Math.abs(row * TileMap.SIZE - (y + h)) < 0.5)) return true;
        }
        return false;
    }

    /** Overridden by the player while holding down on a ledge. */
    protected boolean droppingThrough() {
        return false;
    }

    /** Solid ground (not a ledge) directly ahead of the feet in the given direction. */
    public boolean groundAhead(int dir) {
        double probeX = dir > 0 ? x + w + 2 : x - 2;
        int col = TileMap.tile(probeX), row = TileMap.tile(y + h + 2);
        Tile t = level.map.get(col, row);
        return (t.solid() || t == Tile.PLATFORM) && !level.map.get(col, row - 1).hazard();
    }

    public boolean wallAhead(int dir) {
        double probeX = dir > 0 ? x + w + 2 : x - 2;
        return level.map.solidIn(probeX, y + 2, 1, h - 4);
    }
}
