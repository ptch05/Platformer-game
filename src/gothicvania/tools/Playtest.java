package gothicvania.tools;

import gothicvania.engine.Display;
import gothicvania.engine.Input;
import gothicvania.engine.Input.Action;
import gothicvania.entity.Enemy;
import gothicvania.entity.Entity;
import gothicvania.entity.Player;
import gothicvania.game.EndingScene;
import gothicvania.game.Game;
import gothicvania.game.PlayScene;
import gothicvania.game.RunStats;
import gothicvania.game.TitleScene;
import gothicvania.world.Level;
import java.io.File;
import java.util.EnumSet;
import java.util.Set;
import javax.imageio.ImageIO;

/**
 * Runs a level headlessly from a script of inputs and reports what happened, for
 * checking jumps, gaps and fights without playing by hand.
 *
 * <pre>
 * java -cp out gothicvania.tools.Playtest LEVEL TILE_X TILE_Y OUT_DIR "script..."
 *
 * script steps (space separated):
 *   RIGHT+JUMP:30   hold these actions for 30 frames (actions released afterwards)
 *   wait:60         hold nothing for 60 frames
 *   shot:name       save OUT_DIR/name.png
 *   log             print the hero's position and state
 *   god             make the hero invulnerable
 *   bot:600         a crude auto-fighter chases and swings at the nearest enemy for 600 frames
 *   finish          claim the level's trophy (shows the results card)
 *   title / ending  switch to the title or ending screen
 * </pre>
 * TILE_X/TILE_Y of -1 keeps the level's own start.
 */
public final class Playtest {
    private Playtest() {
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("gv.silent", "true");
        System.setProperty("gv.save", new File(args[3], "save.properties").getPath());
        Input input = new Input();
        Game game = new Game(input, null);
        PlayScene scene = new PlayScene(game, Integer.parseInt(args[0]), new RunStats());
        game.setScene(scene);
        double tx = Double.parseDouble(args[1]), ty = Double.parseDouble(args[2]);
        if (tx >= 0) {
            scene.level().player().placeAt(tx * 16 + 8, (ty + 1) * 16);
            scene.level().camera.snap(tx * 16 + 8, ty * 16);
        }
        int frame = 0;
        for (String step : args[4].trim().split("\\s+")) {
            if (step.equals("log")) {
                log(scene.level(), frame);
            } else if (step.equals("finish")) {
                scene.level().onTrophyClaimed();
            } else if (step.equals("title")) {
                game.setScene(new TitleScene(game));
            } else if (step.equals("ending")) {
                game.setScene(new EndingScene(game, new RunStats()));
            } else if (step.equals("god")) {
                scene.level().player().god = true;
            } else if (step.startsWith("bot:")) {
                int frames = Integer.parseInt(step.substring(4));
                for (int i = 0; i < frames; i++) {
                    botStep(scene.level(), input, frame);
                    input.poll();
                    game.update(Display.STEP);
                    frame++;
                    if (frame % 300 == 0) log(scene.level(), frame);
                }
                for (Action a : Action.values()) input.simulate(a, false);
            } else if (step.startsWith("shot:")) {
                ImageIO.write(Display.renderOffscreen(game), "png", new File(args[3], step.substring(5) + ".png"));
            } else {
                String[] parts = step.split(":");
                int frames = Integer.parseInt(parts[1]);
                Set<Action> held = EnumSet.noneOf(Action.class);
                if (!parts[0].equals("wait")) {
                    for (String a : parts[0].split("\\+")) held.add(Action.valueOf(a));
                }
                for (Action a : held) input.simulate(a, true);
                for (int i = 0; i < frames; i++) {
                    input.poll();
                    game.update(Display.STEP);
                    frame++;
                }
                for (Action a : held) input.simulate(a, false);
            }
        }
        log(scene.level(), frame);
        System.exit(0);
    }

    /** Walks toward the nearest enemy and swings; jumps when it is above, dashes now and then. */
    private static void botStep(Level level, Input input, int frame) {
        Player p = level.player();
        Enemy target = null;
        double best = Double.MAX_VALUE;
        for (Entity e : level.entities()) {
            if (e instanceof Enemy enemy && enemy.isVulnerable() && !e.removed) {
                double d = Math.abs(e.cx() - p.cx()) + Math.abs(e.cy() - p.cy()) * 0.5;
                if (d < best) {
                    best = d;
                    target = enemy;
                }
            }
        }
        for (Action a : Action.values()) input.simulate(a, false);
        if (target == null) return;
        double dx = target.cx() - p.cx();
        boolean inReach = Math.abs(dx) < 34;
        boolean above = target.bottom() < p.y + 4;
        if (!inReach) input.simulate(dx > 0 ? Action.RIGHT : Action.LEFT, true);
        else if (p.facing != (dx > 0 ? 1 : -1)) input.simulate(dx > 0 ? Action.RIGHT : Action.LEFT, true);
        if (inReach && above && p.onGround) input.simulate(Action.JUMP, true);
        if (inReach && frame % 8 == 0) input.simulate(Action.ATTACK, true);
        boolean close = inReach && !above;
        if (frame % 97 == 0) input.simulate(Action.DASH, true);
        if (p.bloodthirst >= Player.BLOODTHIRST_MAX && close) input.simulate(Action.SPECIAL, true);
    }

    private static void log(Level level, int frame) {
        Player p = level.player();
        var boss = level.boss();
        if (boss != null) System.out.printf("           boss hp %d/%d phase %d awake=%b defeated=%b%n", boss.hp(), boss.maxHp(), boss.phase(), boss.isAwake(), boss.isDefeated());
        System.out.printf("frame %4d  tile (%.1f, %.1f)  feet y=%.0f  hp %d/%d  %s%s  ground=%b  kills=%d souls=%d deaths=%d%n",
            frame, p.cx() / 16, p.bottom() / 16 - 1, p.bottom(), p.hp, p.maxHp, p.state(),
            p.isDead() ? " (dead)" : "", p.onGround, level.progress().kills, level.progress().souls, level.progress().deaths);
    }
}
