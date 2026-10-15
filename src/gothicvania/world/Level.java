package gothicvania.world;

import gothicvania.engine.Audio;
import gothicvania.engine.Camera;
import gothicvania.engine.Display;
import gothicvania.engine.Input;
import gothicvania.engine.Lighting;
import gothicvania.engine.Particles;
import gothicvania.entity.Checkpoint;
import gothicvania.entity.CrumbleBlock;
import gothicvania.entity.Enemy;
import gothicvania.entity.Entity;
import gothicvania.entity.FireTrap;
import gothicvania.entity.Fireball;
import gothicvania.entity.Ghost;
import gothicvania.entity.Hound;
import gothicvania.entity.MovingPlatform;
import gothicvania.entity.Pickup;
import gothicvania.entity.Platform;
import gothicvania.entity.Player;
import gothicvania.entity.Prop;
import gothicvania.entity.RisingLava;
import gothicvania.entity.Skeleton;
import gothicvania.entity.Trophy;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A level being played: builds the world from {@link LevelData}, runs every
 * entity, resolves combat and hazards, drives the camera and draws it all with
 * parallax, lighting and particles. Things the surrounding screen needs to know
 * about (death, completion, on-screen messages) go through {@link Listener}.
 */
public final class Level {

    /** How the level reports back to the screen that owns it. */
    public interface Listener {
        void onBanner(String title, String subtitle);

        void onPlayerDied();

        void onLevelComplete();
    }

    private static final Input NO_INPUT = new Input();
    private static final int T = TileMap.SIZE;
    private static final BufferedImage VIGNETTE = vignette();

    public final LevelData data;
    public final TileMap map;
    public final Camera camera = new Camera();
    public final Particles particles = new Particles();
    private final Lighting lighting = new Lighting();
    private final Background background;
    private final LevelProgress progress;
    private final Listener listener;

    private final List<Entity> entities = new ArrayList<>();
    private final List<Entity> pending = new ArrayList<>();
    private final List<Entity> drawOrder = new ArrayList<>();
    private final List<Platform> platforms = new ArrayList<>();
    private final List<int[]> secrets = new ArrayList<>();
    private final List<SecretRoom> secretRooms = new ArrayList<>();
    private final List<int[]> hints = new ArrayList<>();
    private final List<Checkpoint> checkpoints = new ArrayList<>();
    private Player player;

    private Input input = NO_INPUT;
    private double time;
    private double hitstop;
    private double slowScale = 1, slowTime;
    private int flashColor;
    private double flashTime, flashLength;
    private double hazardFade;
    private double deathTimer = -1;
    private double completeTimer = -1;
    private boolean debugHitboxes;


    private int wallSwing = -1;
    private final Set<Integer> wallTilesHit = new HashSet<>();

    public Level(LevelData data, LevelProgress progress, int maxHp, Listener listener) {
        this.data = data;
        this.progress = progress;
        this.listener = listener;
        this.map = new TileMap(data.cols, data.rows, data.theme);
        this.background = new Background(data.theme, data.rows * T);
        build(maxHp);
        camera.setBounds(0, 0, map.widthPx(), map.heightPx());
        camera.snap(player.cx(), player.cy());
    }

    // ------------------------------------------------------------------ construction

    private void build(int maxHp) {
        double startX = 3 * T, startY = 3 * T;
        int checkpointId = 0;
        int secretCount = 0;
        int riseTrigger = -1, riseStopRow = -1;
        for (int row = 0; row < data.rows; row++) {
            for (int col = 0; col < data.cols; col++) {
                char c = data.at(col, row);
                double fx = col * T + T / 2.0, fy = (row + 1) * T; // foot position: bottom-centre of the cell
                double mx = col * T + T / 2.0, my = row * T + T / 2.0; // middle of the cell
                int id = row * data.cols + col;
                switch (c) {
                    case '#' -> map.set(col, row, Tile.SOLID);
                    case '=' -> map.set(col, row, Tile.PLATFORM);
                    case '^' -> map.set(col, row, Tile.SPIKES);
                    case '~' -> map.set(col, row, Tile.LAVA);
                    case 'B' -> map.set(col, row, Tile.BREAKABLE);
                    case 'C' -> add(new CrumbleBlock(col * T, row * T, data.theme));
                    case 'P' -> {
                        startX = fx;
                        startY = fy;
                    }
                    case 'S' -> {
                        Checkpoint cp = new Checkpoint(fx, fy, checkpointId, progress.litCheckpoints.contains(checkpointId));
                        checkpoints.add(cp);
                        add(cp);
                        checkpointId++;
                    }
                    case 's' -> add(new Skeleton(fx, fy, false, true));
                    case 'z' -> add(new Skeleton(fx, fy, true, false));
                    case 'h' -> add(new Hound(fx, fy));
                    case 'g' -> add(new Ghost(mx, my, false));
                    case 'G' -> add(new Ghost(mx, my, true));
                    case 'f', 'F' -> add(new FireTrap(fx, fy, (col * 0.61) % 2.8, c == 'F'));
                    case 'm', 'v' -> addMovingPlatform(col, row, c == 'v');
                    case 'p' -> addPickup(Pickup.Kind.POTION, mx, my, id);
                    case 'a' -> addPickup(Pickup.Kind.ARMOUR, mx, my, id);
                    case 'o' -> addPickup(Pickup.Kind.SOUL, mx, my, id);
                    case 'H' -> addPickup(Pickup.Kind.HEART, mx, my, id);
                    case '!' -> {
                        secrets.add(new int[]{col, row, id});
                        secretCount++;
                    }
                    case 'T' -> add(new Trophy(fx, fy, false));
                    case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> hints.add(new int[]{col, row, id, c});
                    case 'R' -> riseTrigger = col;
                    case 'Z' -> riseStopRow = row;
                    case 't' -> add(new Prop("props/tree-" + (1 + Math.floorMod(id * 7, 3)) + ".png", fx, fy, false, (id & 1) == 0));
                    case 'r' -> add(new Prop("props/stone-" + (1 + Math.floorMod(id * 5, 4)) + ".png", fx, fy, false, false));
                    case 'b' -> add(new Prop((id & 1) == 0 ? "props/bush-large.png" : "props/bush-small.png", fx, fy, false, (id & 2) == 0));
                    case 'k' -> add(new Prop("props/bush-small.png", fx, fy, true, (id & 1) == 0));
                    case 'i' -> add(new Prop("props/pillar.png", fx, fy, false, false));
                    case 'I' -> add(new Prop("props/pillar-broken.png", fx, fy, false, (id & 1) == 0));
                    default -> {
                    }
                }
            }
        }
        progress.totalSecrets = secretCount;
        for (int[] s : secrets) {
            SecretRoom room = SecretRoom.find(map, s[0], s[1], s[2]);
            if (room != null) {
                if (progress.secretsFound.contains(s[2])) room.reveal = 1;
                secretRooms.add(room);
            }
        }
        if (riseTrigger >= 0) {
            add(new RisingLava(riseTrigger * T, map.heightPx() + 40, Math.max(0, riseStopRow) * T, map.widthPx()));
        }

        player = new Player(startX, startY, maxHp);
        if (progress.checkpoint >= 0 && progress.checkpoint < checkpoints.size()) {
            Checkpoint cp = checkpoints.get(progress.checkpoint);
            player.placeAt(cp.cx() + 20, cp.bottom());
        }
        add(player);
        flushPending();
    }

    private void addPickup(Pickup.Kind kind, double cx, double cy, int id) {
        if (!progress.collected.contains(id)) add(new Pickup(kind, cx, cy, id));
    }

    /** Moving platforms travel to the {@code :} markers on either side of them (row for 'm', column for 'v'). */
    private void addMovingPlatform(int col, int row, boolean vertical) {
        int a = vertical ? row : col, b = a;
        for (int i = a - 1; i >= 0 && data.at(vertical ? col : i, vertical ? i : row) != '#'; i--) {
            if (data.at(vertical ? col : i, vertical ? i : row) == ':') a = i;
        }
        int limit = vertical ? data.rows : data.cols;
        for (int i = b + 1; i < limit && data.at(vertical ? col : i, vertical ? i : row) != '#'; i++) {
            if (data.at(vertical ? col : i, vertical ? i : row) == ':') b = i;
        }
        // the 64px platform is centred on its marker column
        double ax = vertical ? col * T + 8 - 32 : a * T + 8 - 32, ay = vertical ? a * T : row * T;
        double bx = vertical ? ax : b * T + 8 - 32, by = vertical ? b * T : ay;
        int startOffset = vertical ? row - a : col - a;
        int length = Math.max(1, (vertical ? b - a : b - a));
        double phase = Math.acos(1 - 2 * Math.min(1, startOffset / (double) length)) / (2 * Math.PI);
        add(new MovingPlatform(ax, ay, bx, by, phase, data.theme));
    }

    private void add(Entity e) {
        e.attach(this);
        pending.add(e);
    }

    private void flushPending() {
        for (Entity e : pending) {
            entities.add(e);
            if (e instanceof Platform p) platforms.add(p);
        }
        pending.clear();
    }

    // ------------------------------------------------------------------ API used by entities

    public Player player() {
        return player;
    }

    public Input input() {
        return input;
    }

    public List<Entity> entities() {
        return entities;
    }

    public List<Platform> platforms() {
        return platforms;
    }

    public void spawn(Entity e) {
        add(e);
    }

    public void hitstop(double seconds) {
        hitstop = Math.max(hitstop, seconds);
    }

    public void slowMotion(double scale, double seconds) {
        slowScale = scale;
        slowTime = seconds;
    }

    public void flash(int rgb, double seconds) {
        flashColor = rgb;
        flashTime = flashLength = seconds;
    }

    public void banner(String title, String subtitle) {
        listener.onBanner(title, subtitle);
    }

    public void onEnemyKilled(Enemy enemy, int souls) {
        progress.kills++;
        player.onHitLanded(true);
        for (int i = 0; i < souls; i++) add(Pickup.droppedSoul(enemy.cx(), enemy.cy()));
    }

    public void onPickup(Pickup pickup) {
        if (pickup.id >= 0) progress.collected.add(pickup.id);
        if (pickup.kind == Pickup.Kind.SOUL) progress.souls++;
        if (pickup.kind == Pickup.Kind.HEART) progress.hearts++;
    }

    public void onCheckpoint(Checkpoint cp) {
        progress.checkpoint = cp.id;
        progress.litCheckpoints.add(cp.id);
    }

    public void onPlayerDamaged(int amount) {
        progress.damageTaken += amount;
        flash(0xB0182A, 0.12);
    }

    public void onPlayerDied() {
        if (deathTimer < 0) deathTimer = 0;
    }

    /** Spikes, lava (tile != null) or a bottomless pit (tile == null). */
    public void onPlayerHazard(Tile tile) {
        if (hazardFade > 0 || player.isDead() || completeTimer >= 0) return;
        int damage = tile == Tile.LAVA ? 30 : 20;
        if (player.god) damage = 0;
        if (player.armoured()) damage /= 2;
        player.hp -= damage;
        progress.damageTaken += damage;
        Audio.get().playVaried("sounds/player/hurt", 1f);
        camera.shake(0.4);
        if (tile == Tile.LAVA) {
            particles.emit(player.cx(), player.bottom(), 20, Particles.burst().speed(40, 140).direction(-90, 90)
                .gravity(300).life(0.3, 0.7).colors(0xFFB02E, 0xE8461E, 0xFFF27A));
        }
        if (player.hp <= 0) {
            player.hp = 0;
            player.die();
        } else {
            hazardFade = 0.5;
        }
    }

    public void onTrophyClaimed() {
        if (completeTimer >= 0) return;
        completeTimer = 0;
        Audio.get().stopMusic(0.5f);
        Audio.get().sting("music/victory");
        flash(0xFFE27A, 0.4);
        particles.emit(player.cx(), player.cy(), 60, Particles.burst().speed(40, 200).drag(1.5).life(0.5, 1.4)
            .colors(0xFFE27A, 0xFFFFFF, 0xFFB02E).size(1, 3));
    }

    public void setDebugHitboxes(boolean on) {
        debugHitboxes = on;
    }

    public LevelProgress progress() {
        return progress;
    }

    // ------------------------------------------------------------------ update

    public void update(double dt, Input in) {
        boolean locked = deathTimer >= 0 || completeTimer >= 0 || hazardFade > 0;
        input = locked ? NO_INPUT : in;

        if (flashTime > 0) flashTime -= dt;
        if (deathTimer >= 0) {
            deathTimer += dt;
            if (deathTimer > 1.8) {
                deathTimer = -100; // report once
                listener.onPlayerDied();
            }
        }
        if (completeTimer >= 0) {
            completeTimer += dt;
            if (completeTimer > 3.2) {
                completeTimer = -100;
                listener.onLevelComplete();
            }
        }
        if (hazardFade > 0) {
            double before = hazardFade;
            hazardFade -= dt;
            if (before > 0.25 && hazardFade <= 0.25) {
                player.respawnAtSafeGround();
                camera.snap(player.cx(), player.cy());
            }
            if (hazardFade > 0) {
                particles.update(dt);
                return;
            }
        }
        if (hitstop > 0) {
            hitstop -= dt;
            return;
        }
        if (slowTime > 0) {
            slowTime -= dt;
            dt *= slowScale;
        }
        time += dt;
        if (completeTimer < 0 && deathTimer < 0) progress.time += dt;

        flushPending();
        for (SecretRoom room : secretRooms) if (room.revealing) room.reveal = Math.min(1, room.reveal + dt * 2);
        for (Platform p : platforms) p.update(dt);
        double camCenterX = camera.rawX() + Display.WIDTH / 2.0;
        for (Entity e : entities) {
            if (e.removed || e instanceof Platform) continue;
            if (e instanceof Enemy && Math.abs(e.cx() - camCenterX) > 520) continue;
            if (completeTimer >= 0 && e == player) continue;
            e.update(dt);
        }
        resolveCombat();
        checkSecrets();
        checkHints();
        entities.removeIf(e -> e.removed);
        platforms.removeIf(e -> e.removed);
        flushPending();

        camera.follow(player.cx(), player.bottom() - 18, player.facing, dt);
        particles.update(dt);
        emitAmbience(dt);
    }

    private void resolveCombat() {
        if (player.isDead()) return;
        double[] sword = player.attackBox();
        boolean landed = false;
        for (Entity e : entities) {
            if (e.removed) continue;
            if (sword != null) {
                if (e instanceof Enemy enemy && enemy.overlaps(sword[0], sword[1], sword[2], sword[3])) {
                    if (enemy.strike(player.swingId(), player.damageMultiplier(), player.cx())) {
                        landed = true;
                        if (!enemy.removed) player.onHitLanded(false);
                    }
                } else if (e instanceof Fireball f && !f.isReflected() && f.overlaps(sword[0], sword[1], sword[2], sword[3])) {
                    f.reflect(player.facing);
                }
            }
            if (e instanceof Enemy enemy && enemy.isHarmful() && enemy.overlaps(player)) {
                player.hurt(enemy.contactDamage(), enemy.cx());
            } else if (e instanceof Fireball f) {
                if (!f.isReflected() && f.overlaps(player)) {
                    if (player.hurt(f.damage(), f.cx())) f.burst();
                } else if (f.isReflected()) {
                    for (Entity other : entities) {
                        if (other instanceof Enemy enemy && enemy.isVulnerable() && f.overlaps(enemy)) {
                            enemy.damage(3, f.cx());
                            f.burst();
                            break;
                        }
                    }
                }
            } else if (e instanceof FireTrap trap && trap.isBurning() && trap.overlaps(player)) {
                player.hurt(20, trap.cx());
            }
        }
        if (landed) {
            hitstop(0.05);
            camera.shake(0.12);
        }
        if (sword != null) strikeWalls(sword);
    }

    /** Breakable walls take one hit per tile per swing. */
    private void strikeWalls(double[] box) {
        if (player.swingId() != wallSwing) {
            wallSwing = player.swingId();
            wallTilesHit.clear();
        }
        int x0 = TileMap.tile(box[0]), x1 = TileMap.tile(box[0] + box[2]);
        int y0 = TileMap.tile(box[1]), y1 = TileMap.tile(box[1] + box[3]);
        for (int ty = y0; ty <= y1; ty++) {
            for (int tx = x0; tx <= x1; tx++) {
                if (map.get(tx, ty) != Tile.BREAKABLE || !wallTilesHit.add(ty * map.cols + tx)) continue;
                boolean broke = map.strikeBreakable(tx, ty);
                Audio.get().playVaried("sounds/player/crouch", broke ? 0.9f : 0.5f);
                camera.shake(broke ? 0.25 : 0.08);
                particles.emit(tx * T + 8, ty * T + 8, broke ? 22 : 6, Particles.burst().speed(30, 120).gravity(500)
                    .life(0.3, 0.8).colors(0x4A3424, 0x6E4E36, 0x2C1E16, 0x8F8A9E).size(1, 3).area(14, 14));
            }
        }
    }

    private void checkSecrets() {
        for (int[] s : secrets) {
            if (progress.secretsFound.contains(s[2])) continue;
            if (player.overlaps(s[0] * T - 8, s[1] * T - 8, T + 16, T + 16)) {
                progress.secretsFound.add(s[2]);
                for (SecretRoom room : secretRooms) if (room.id == s[2]) room.revealing = true;
                Audio.get().play("sounds/collectibles/armour", 0.8f, 1.25f);
                banner("SECRET FOUND", progress.secretsFound.size() + " OF " + progress.totalSecrets);
            }
        }
    }

    private void checkHints() {
        for (int[] h : hints) {
            if (progress.hintsShown.contains(h[2])) continue;
            if (Math.abs(player.cx() - (h[0] * T + 8)) < 12 && Math.abs(player.bottom() - (h[1] + 1) * T) < 4 * T) {
                progress.hintsShown.add(h[2]);
                String text = data.hint((char) h[3]);
                if (text != null) banner(null, text);
            }
        }
    }

    private void emitAmbience(double dt) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        double cx = camera.rawX(), cy = camera.rawY();
        switch (data.theme.ambience) {
            case FOG -> {
                if (r.nextDouble() < dt * 6) {
                    particles.emit(cx + r.nextDouble() * Display.WIDTH, cy + Display.HEIGHT * (0.5 + r.nextDouble() * 0.5), 1,
                        Particles.burst().speed(4, 12).direction(r.nextBoolean() ? 0 : 180, 20).life(3, 6).size(3, 6)
                            .colors(0x2A2440, 0x342C4E).fade());
                }
            }
            case DUST -> {
                if (r.nextDouble() < dt * 10) {
                    particles.emit(cx + r.nextDouble() * Display.WIDTH, cy + r.nextDouble() * Display.HEIGHT, 1,
                        Particles.burst().speed(2, 8).life(2, 4).size(1, 1).colors(0x8A9AB0, 0x5A6A80).fade());
                }
            }
            case EMBERS -> {
                if (r.nextDouble() < dt * 14) {
                    particles.emit(cx + r.nextDouble() * Display.WIDTH, cy + Display.HEIGHT + 4, 1,
                        Particles.burst().speed(15, 45).direction(-90, 50).life(2, 5).size(1, 2)
                            .colors(0xFF8A2E, 0xFFB02E, 0xE8461E).fade());
                }
            }
        }
        // lava bubbles
        int tx0 = TileMap.tile(cx), tx1 = TileMap.tile(cx + Display.WIDTH);
        int ty0 = TileMap.tile(cy), ty1 = TileMap.tile(cy + Display.HEIGHT);
        if (r.nextDouble() < dt * 20) {
            int tx = tx0 + r.nextInt(Math.max(1, tx1 - tx0 + 1));
            for (int ty = ty0; ty <= ty1; ty++) {
                if (map.get(tx, ty) == Tile.LAVA && map.get(tx, ty - 1) != Tile.LAVA) {
                    particles.emit(tx * T + r.nextInt(T), ty * T + 2, 2, Particles.burst().speed(20, 70).direction(-90, 50)
                        .gravity(120).life(0.4, 1.0).colors(0xFFB02E, 0xFFF27A, 0xE8461E));
                    break;
                }
            }
        }
    }

    // ------------------------------------------------------------------ rendering

    public void render(Graphics2D g) {
        int camX = camera.x(), camY = camera.y();
        background.render(g, camX, camY);

        drawOrder.clear();
        for (Entity e : entities) {
            if (!e.removed && (e instanceof RisingLava || camera.isVisible(e.x, e.y, e.w, e.h, 180))) drawOrder.add(e);
        }
        drawOrder.sort(Comparator.comparingInt(Entity::layer));

        g.translate(-camX, -camY);
        int i = 0;
        for (; i < drawOrder.size() && drawOrder.get(i).layer() < 5; i++) drawOrder.get(i).render(g);
        g.translate(camX, camY);
        map.render(g, camX, camY, time);
        g.translate(-camX, -camY);
        for (; i < drawOrder.size(); i++) drawOrder.get(i).render(g);
        for (SecretRoom room : secretRooms) room.render(g, map);
        particles.render(g, 0, 0);
        g.translate(camX, camY);

        lighting.begin(data.theme.darkness);
        for (Entity e : drawOrder) e.emitLight(lighting, camX, camY);
        lightLava(camX, camY);
        lighting.end(g);

        g.translate(-camX, -camY);
        for (Entity e : drawOrder) e.renderOverlay(g);
        if (debugHitboxes) drawHitboxes(g);
        g.translate(camX, camY);

        g.drawImage(VIGNETTE, 0, 0, null);
        if (flashTime > 0) {
            int a = (int) (Math.min(1, flashTime / flashLength) * 110);
            g.setColor(new Color((a << 24) | flashColor, true));
            g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);
        }
        if (hazardFade > 0) {
            double t = hazardFade > 0.25 ? (0.5 - hazardFade) / 0.25 : hazardFade / 0.25;
            g.setColor(new Color(0, 0, 0, (int) (255 * Math.max(0, Math.min(1, t)))));
            g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);
        }
        if (deathTimer > 0.8) {
            g.setColor(new Color(0, 0, 0, (int) (255 * Math.min(1, (deathTimer - 0.8) / 1.0))));
            g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);
        }
    }

    /** Draws the whole level (tiles and entities, no lighting) for the map overview tool. */
    public void renderOverview(Graphics2D g) {
        map.renderAll(g);
        List<Entity> sorted = new ArrayList<>(entities);
        sorted.sort(Comparator.comparingInt(Entity::layer));
        for (Entity e : sorted) e.render(g);
        g.setColor(new Color(255, 255, 0, 140));
        for (int[] h : hints) g.fillRect(h[0] * T + 6, h[1] * T, 4, T);
        g.setColor(new Color(0, 255, 255, 140));
        for (int[] s : secrets) g.drawRect(s[0] * T, s[1] * T, T, T);
    }

    private void lightLava(int camX, int camY) {
        int tx0 = TileMap.tile(camX - 40), tx1 = TileMap.tile(camX + Display.WIDTH + 40);
        int ty0 = TileMap.tile(camY - 40), ty1 = TileMap.tile(camY + Display.HEIGHT + 40);
        for (int ty = ty0; ty <= ty1; ty++) {
            for (int tx = tx0; tx <= tx1; tx += 2) {
                if (map.get(tx, ty) == Tile.LAVA && map.get(tx, ty - 1) != Tile.LAVA) {
                    lighting.light(tx * T + 8 - camX, ty * T + 4 - camY, 44, 0.75f, (tx & 2) == 0 ? 0xFF6A1E : -1);
                }
            }
        }
    }

    private void drawHitboxes(Graphics2D g) {
        g.setColor(new Color(0, 255, 0, 160));
        for (Entity e : entities) g.drawRect((int) e.x, (int) e.y, (int) e.w, (int) e.h);
        double[] sword = player.attackBox();
        g.setColor(new Color(255, 0, 0, 180));
        if (sword != null) g.drawRect((int) sword[0], (int) sword[1], (int) sword[2], (int) sword[3]);
    }

    private static BufferedImage vignette() {
        BufferedImage img = new BufferedImage(Display.WIDTH, Display.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        float r = Display.WIDTH * 0.75f;
        g.setPaint(new RadialGradientPaint(new Point2D.Float(Display.WIDTH / 2f, Display.HEIGHT / 2f), r,
            new float[]{0.55f, 1f}, new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 150)},
            MultipleGradientPaint.CycleMethod.NO_CYCLE));
        g.fillRect(0, 0, Display.WIDTH, Display.HEIGHT);
        g.dispose();
        return img;
    }
}
