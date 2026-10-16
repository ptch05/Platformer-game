package gothicvania.entity;

/** Something entities can stand on that isn't part of the tile map (moving or crumbling ledges). */
public abstract class Platform extends Entity {
    protected Platform(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    /** Whether its top currently supports things standing on it. */
    public boolean isSolidTop() {
        return true;
    }

    /** Moves everything standing on this platform along with it. */
    protected void carryRiders(double dx, double dy) {
        if (dx == 0 && dy == 0) return;
        for (Entity e : level.entities()) {
            if (e.riding == this) e.carry(dx, dy);
        }
    }

    @Override
    public int layer() {
        return 5;
    }
}
