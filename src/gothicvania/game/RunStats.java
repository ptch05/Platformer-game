package gothicvania.game;

import gothicvania.world.LevelProgress;

/** Totals carried across levels during one playthrough. */
public final class RunStats {
    public static final int BASE_HEALTH = 100;

    public int maxHp = BASE_HEALTH;
    public int kills;
    public int souls;
    public int deaths;
    public int secrets;
    public int totalSecrets;
    public double time;

    public void add(LevelProgress p) {
        kills += p.kills;
        souls += p.souls;
        deaths += p.deaths;
        secrets += p.secretsFound.size();
        totalSecrets += p.totalSecrets;
        time += p.time;
    }

    public static String clock(double seconds) {
        int s = (int) seconds;
        return String.format("%d:%02d", s / 60, s % 60);
    }
}
