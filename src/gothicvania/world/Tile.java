package gothicvania.world;

/** What occupies one 16x16 cell of a level. */
public enum Tile {
    EMPTY,
    /** Ground you can stand on and can't pass through. */
    SOLID,
    /** A ledge you can jump up through and drop down from. */
    PLATFORM,
    /** Hurts on touch and sends you back to safe ground. */
    SPIKES,
    /** Like spikes, but hotter. */
    LAVA,
    /** Solid until struck twice; hides secrets. */
    BREAKABLE,
    /** Bars that seal the boss arena while the fight is on. */
    GATE;

    public boolean solid() {
        return this == SOLID || this == BREAKABLE || this == GATE;
    }

    public boolean hazard() {
        return this == SPIKES || this == LAVA;
    }
}
