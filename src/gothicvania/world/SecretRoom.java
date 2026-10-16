package gothicvania.world;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The enclosed pocket of space around a secret marker. Until the hero finds it,
 * it is drawn over with rock so it looks like part of the wall; then it fades away.
 */
final class SecretRoom {
    private static final int MAX_CELLS = 80;

    final int id;
    final List<int[]> cells;
    boolean revealing;
    double reveal;

    private SecretRoom(int id, List<int[]> cells) {
        this.id = id;
        this.cells = cells;
    }

    /** Flood-fills open cells from the marker; returns null if the space isn't enclosed (e.g. open sky). */
    static SecretRoom find(TileMap map, int col, int row, int id) {
        List<int[]> cells = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{col, row});
        seen.add(row * map.cols + col);
        while (!queue.isEmpty()) {
            int[] c = queue.poll();
            cells.add(c);
            if (cells.size() > MAX_CELLS) return null;
            int[][] next = {{c[0] + 1, c[1]}, {c[0] - 1, c[1]}, {c[0], c[1] + 1}, {c[0], c[1] - 1}};
            for (int[] n : next) {
                if (n[0] < 0 || n[1] < 0 || n[0] >= map.cols || n[1] >= map.rows) return null;
                if (map.get(n[0], n[1]).solid() || !seen.add(n[1] * map.cols + n[0])) continue;
                queue.add(n);
            }
        }
        return new SecretRoom(id, cells);
    }

    void render(Graphics2D g, TileMap map) {
        if (reveal >= 1) return;
        Composite old = g.getComposite();
        if (reveal > 0) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1 - reveal)));
        for (int[] c : cells) map.drawCover(g, c[0], c[1]);
        g.setComposite(old);
    }
}
