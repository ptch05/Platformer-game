package gothicvania.world;

import java.util.HashSet;
import java.util.Set;

/**
 * What the hero has achieved in the current attempt at a level. It survives
 * deaths (the level is rebuilt from it), so collected items stay collected and
 * the last checkpoint is remembered.
 */
public final class LevelProgress {
    public final Set<Integer> collected = new HashSet<>();
    public final Set<Integer> secretsFound = new HashSet<>();
    public final Set<Integer> litCheckpoints = new HashSet<>();
    public final Set<Integer> hintsShown = new HashSet<>();
    public int checkpoint = -1;
    public int kills;
    public int souls;
    public int deaths;
    public int damageTaken;
    public double time;
    public int totalSecrets;
    public int hearts;

    /** S for a flawless clear with every secret, then A-C by deaths. */
    public String rank() {
        if (deaths == 0 && secretsFound.size() >= totalSecrets && damageTaken < 60) return "S";
        if (deaths == 0) return "A";
        if (deaths <= 2) return "B";
        return "C";
    }
}
