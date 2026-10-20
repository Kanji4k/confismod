package client.confis;

import net.minecraft.class_332;
import net.minecraft.class_437;

/** Launcher for everything new. Opened from the TOOLS button in the main menu. */
public class ToolsScreen extends ConfisScreen {
    public ToolsScreen(class_437 parent) { super("Confis0oooooo Tools", parent); }

    @Override protected int panelW() { return 330; }
    @Override protected int panelH() { return 150; }

    @Override
    protected void build() {
        int w = (pw - 16 - 8) / 2, h = 24, x = px + 8, y = py + 34;
        btn("Friends", x, y, w, h, () -> mc.method_1507(new FriendsScreen(this, null)));
        btn("Item Guide", x + w + 8, y, w, h, () -> mc.method_1507(new ItemGuideScreen(this)));
        y += h + 6;
        btn("Online Players", x, y, w, h, () -> mc.method_1507(new PlayerSearchScreen(this)));
        btn("Item Drop Scale", x + w + 8, y, w, h, () -> mc.method_1507(new ItemScaleScreen(this)));
        y += h + 6;
        btn("Add Waypoint (coords)", x, y, w, h, () -> mc.method_1507(new WaypointAddScreen(this)));
        btn("Hitboxes & Nametags", x + w + 8, y, w, h, () -> mc.method_1507(new HitboxScreen(this)));
    }

    @Override protected void draw(class_332 g, int mx, int my) {}
}
