package client.confis;

import java.util.Map;
import java.util.WeakHashMap;

/** Looks up the size multiplier for a dropped item, and carries it from the extract step to the draw step. */
public final class DropScale {
    public static final float MIN = 0.25f, MAX = 4.0f;
    /** render-state object -> scale (weak so entities that vanish are collected). */
    public static final Map<Object, Float> BY_STATE = new WeakHashMap<>();

    private DropScale() {}

    public static float forId(String id) {
        Config c = Config.get();
        Float v = c.itemScales.get(id);
        return v != null ? v : c.dropScale;
    }

    public static float step(float v, int dir) {
        float n = Math.round((v + dir * 0.25f) * 4f) / 4f;
        return Math.max(MIN, Math.min(MAX, n));
    }
}
