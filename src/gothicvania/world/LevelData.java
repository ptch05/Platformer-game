package gothicvania.world;

import gothicvania.engine.Assets;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A level as written in a {@code levels/*.txt} file: a few {@code key: value}
 * header lines, a {@code ---} separator, then the map drawn in ASCII. See
 * {@code levels/README.md} for the character legend.
 */
public final class LevelData {
    public final String file;
    public final String name;
    public final String subtitle;
    public final Theme theme;
    public final String music;
    private final Map<String, String> meta;
    public final char[][] grid; // [row][col]
    public final int cols;
    public final int rows;

    private LevelData(String file, Map<String, String> meta, List<String> lines) {
        this.file = file;
        this.name = meta.getOrDefault("name", file);
        this.subtitle = meta.getOrDefault("subtitle", "");
        this.theme = Theme.byId(meta.getOrDefault("theme", "cemetery"));
        this.music = meta.getOrDefault("music", theme.music);
        this.meta = meta;
        int width = lines.stream().mapToInt(String::length).max().orElse(0);
        rows = lines.size();
        cols = width;
        grid = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            String line = lines.get(r);
            for (int c = 0; c < cols; c++) grid[r][c] = c < line.length() ? line.charAt(c) : ' ';
        }
    }

    /** Text for a hint trigger digit ('1'..'9'), from a {@code hint.N:} header line. */
    public String hint(char digit) {
        return meta.get("hint." + digit);
    }

    public char at(int col, int row) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return ' ';
        return grid[row][col];
    }

    public static LevelData parse(String file, List<String> text) {
        Map<String, String> meta = new HashMap<>();
        List<String> map = new ArrayList<>();
        boolean inMap = false;
        for (String line : text) {
            if (!inMap) {
                if (line.strip().equals("---")) {
                    inMap = true;
                } else if (line.contains(":") && !line.startsWith("#")) {
                    int i = line.indexOf(':');
                    meta.put(line.substring(0, i).strip().toLowerCase(), line.substring(i + 1).strip());
                }
            } else {
                map.add(line.replace('\t', ' '));
            }
        }
        while (!map.isEmpty() && map.get(map.size() - 1).isBlank()) map.remove(map.size() - 1);
        if (map.isEmpty()) throw new IllegalArgumentException(file + " has no map (missing '---'?)");
        return new LevelData(file, meta, map);
    }

    /** Loads every {@code levels/*.txt} in file-name order. */
    public static List<LevelData> loadAll() {
        Path dir = Assets.projectFile("levels");
        try (Stream<Path> files = Files.list(dir)) {
            List<LevelData> out = new ArrayList<>();
            for (Path p : files.filter(p -> p.toString().endsWith(".txt")).sorted().toList()) {
                out.add(parse(p.getFileName().toString(), Files.readAllLines(p, StandardCharsets.UTF_8)));
            }
            if (out.isEmpty()) throw new IllegalStateException("No levels found in " + dir);
            return out;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read levels from " + dir, e);
        }
    }
}
