package gothicvania.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Keyboard input mapped onto game actions. Key events arrive on the Swing thread;
 * {@link #poll()} snapshots them once per update so the game sees a stable state,
 * and a press is never lost even if the key is released before the next frame.
 */
public final class Input implements KeyListener {

    public enum Action {
        LEFT, RIGHT, UP, DOWN, JUMP, ATTACK, SPECIAL, DASH,
        PAUSE, CONFIRM, BACK, MUTE, FULLSCREEN,
        DEBUG_HITBOXES, DEBUG_GOD, DEBUG_SKIP
    }

    private static final Map<Action, List<Integer>> BINDINGS = new EnumMap<>(Action.class);

    static {
        BINDINGS.put(Action.LEFT, List.of(KeyEvent.VK_A, KeyEvent.VK_LEFT));
        BINDINGS.put(Action.RIGHT, List.of(KeyEvent.VK_D, KeyEvent.VK_RIGHT));
        BINDINGS.put(Action.UP, List.of(KeyEvent.VK_W, KeyEvent.VK_UP));
        BINDINGS.put(Action.DOWN, List.of(KeyEvent.VK_S, KeyEvent.VK_DOWN));
        BINDINGS.put(Action.JUMP, List.of(KeyEvent.VK_SPACE, KeyEvent.VK_W, KeyEvent.VK_UP, KeyEvent.VK_Z));
        BINDINGS.put(Action.ATTACK, List.of(KeyEvent.VK_K, KeyEvent.VK_J, KeyEvent.VK_X));
        BINDINGS.put(Action.SPECIAL, List.of(KeyEvent.VK_G, KeyEvent.VK_L, KeyEvent.VK_C));
        BINDINGS.put(Action.DASH, List.of(KeyEvent.VK_SHIFT, KeyEvent.VK_I, KeyEvent.VK_V));
        BINDINGS.put(Action.PAUSE, List.of(KeyEvent.VK_ESCAPE, KeyEvent.VK_P));
        BINDINGS.put(Action.CONFIRM, List.of(KeyEvent.VK_ENTER, KeyEvent.VK_SPACE, KeyEvent.VK_K, KeyEvent.VK_X));
        BINDINGS.put(Action.BACK, List.of(KeyEvent.VK_ESCAPE, KeyEvent.VK_BACK_SPACE));
        BINDINGS.put(Action.MUTE, List.of(KeyEvent.VK_M));
        BINDINGS.put(Action.FULLSCREEN, List.of(KeyEvent.VK_F11, KeyEvent.VK_F));
        BINDINGS.put(Action.DEBUG_HITBOXES, List.of(KeyEvent.VK_F1));
        BINDINGS.put(Action.DEBUG_GOD, List.of(KeyEvent.VK_F2));
        BINDINGS.put(Action.DEBUG_SKIP, List.of(KeyEvent.VK_F3));
    }

    private static final int COUNT = Action.values().length;

    // Written by the Swing thread, guarded by this.
    private final Set<Integer> keysDown = new HashSet<>();
    private final boolean[] latchedPress = new boolean[COUNT];

    // Read by the game thread after poll().
    private final boolean[] down = new boolean[COUNT];
    private final boolean[] pressed = new boolean[COUNT];
    private final boolean[] previous = new boolean[COUNT];

    public synchronized void poll() {
        for (Action action : Action.values()) {
            int i = action.ordinal();
            previous[i] = down[i];
            boolean held = false;
            for (int key : BINDINGS.get(action)) held |= keysDown.contains(key);
            pressed[i] = latchedPress[i];
            down[i] = held || latchedPress[i];
            latchedPress[i] = false;
        }
    }

    /** True while the action is held. */
    public boolean down(Action action) {
        return down[action.ordinal()];
    }

    /** True only on the update in which the action was pressed. */
    public boolean pressed(Action action) {
        return pressed[action.ordinal()];
    }

    /** True only on the update in which the action was let go. */
    public boolean released(Action action) {
        return previous[action.ordinal()] && !down[action.ordinal()];
    }

    /** Horizontal axis: -1, 0 or 1. */
    public int axisX() {
        return (down(Action.RIGHT) ? 1 : 0) - (down(Action.LEFT) ? 1 : 0);
    }

    /** Forgets everything held (used when focus moves between scenes so keys don't leak through). */
    public synchronized void clear() {
        keysDown.clear();
        Arrays.fill(latchedPress, false);
        Arrays.fill(down, false);
        Arrays.fill(pressed, false);
    }

    /** Presses or releases an action's primary key, as if typed (used by the play-test tool). */
    public void simulate(Action action, boolean down) {
        int key = BINDINGS.get(action).get(0);
        if (down) press(key);
        else release(key);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        press(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        release(e.getKeyCode());
    }

    private synchronized void press(int code) {
        if (!keysDown.add(code)) return; // ignore OS auto-repeat
        for (Action action : Action.values()) {
            if (BINDINGS.get(action).contains(code)) latchedPress[action.ordinal()] = true;
        }
    }

    private synchronized void release(int code) {
        keysDown.remove(code);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
