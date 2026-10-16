package gothicvania.game;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Remembers unlocked levels and best results between sessions in {@code ~/.gothicvania/save.properties}. */
public final class SaveData {
    private final Path file;
    private final Properties props = new Properties();

    public SaveData() {
        String override = System.getProperty("gv.save");
        file = override != null ? Path.of(override) : Path.of(System.getProperty("user.home"), ".gothicvania", "save.properties");
        if (Files.exists(file)) {
            try (InputStream in = Files.newInputStream(file)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("Could not read save: " + e.getMessage());
            }
        }
    }

    /** Number of levels the player may start from (at least 1). */
    public int unlocked() {
        return Math.max(1, Integer.parseInt(props.getProperty("unlocked", "1")));
    }

    public String bestRank(int level) {
        return props.getProperty("level." + level + ".rank", "-");
    }

    public void recordClear(int level, String rank, int levelCount) {
        props.setProperty("unlocked", String.valueOf(Math.max(unlocked(), Math.min(levelCount, level + 2))));
        String old = props.getProperty("level." + level + ".rank");
        if (old == null || "SABC".indexOf(rank) < "SABC".indexOf(old)) props.setProperty("level." + level + ".rank", rank);
        save();
    }

    private void save() {
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) {
                props.store(out, "GothicVania save");
            }
        } catch (IOException e) {
            System.err.println("Could not write save: " + e.getMessage());
        }
    }
}
