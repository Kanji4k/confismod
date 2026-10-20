package client.confis;

import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;

/** Create a waypoint by typing its name and X / Y / Z (and choosing the dimension). */
public class WaypointAddScreen extends ConfisScreen {
    private static final String[] DIMS = {"overworld", "the_nether", "the_end"};
    private static final String[] DIM_LABEL = {"Overworld", "Nether", "End"};

    private final class_342 name, x, y, z;
    private int dim = 0, colorIdx;
    private String error = "";

    public WaypointAddScreen(class_437 parent) {
        super("Add Waypoint", parent);
        var f = mc.field_1772;
        name = new class_342(f, 0, 0, 100, 14, class_2561.method_43470("Name"));
        x = new class_342(f, 0, 0, 100, 14, class_2561.method_43470("X"));
        y = new class_342(f, 0, 0, 100, 14, class_2561.method_43470("Y"));
        z = new class_342(f, 0, 0, 100, 14, class_2561.method_43470("Z"));
        name.method_1880(24);
        name.method_1852("Waypoint " + (Config.get().waypointList.size() + 1));
        for (class_342 b : new class_342[]{x, y, z}) {
            b.method_1880(9);
            b.method_1890(s -> s.matches("-?\\d*"));
        }
        if (mc.field_1724 != null) {
            x.method_1852(String.valueOf(mc.field_1724.method_31477()));
            y.method_1852(String.valueOf(mc.field_1724.method_31478()));
            z.method_1852(String.valueOf(mc.field_1724.method_31479()));
        }
        colorIdx = Config.get().waypointList.size() % Waypoints.PALETTE.length;
    }

    @Override protected int panelW() { return 300; }
    @Override protected int panelH() { return 190; }

    @Override
    protected void build() {
        int fx = px + 60, fw = pw - 70;
        place(name, fx, py + 34, fw);
        place(x, fx, py + 54, fw);
        place(y, fx, py + 74, fw);
        place(z, fx, py + 94, fw);
        class_4185 d = btn(DIM_LABEL[dim], fx, py + 114, 90, 14, () -> { dim = (dim + 1) % 3; markDirty(); });
        btn("Color", fx + 96, py + 114, 60, 14, () -> { colorIdx = (colorIdx + 1) % Waypoints.PALETTE.length; });
        btn("Use my position", px + 8, py + ph - 24, 100, 16, () -> {
            if (mc.field_1724 == null) return;
            x.method_1852(String.valueOf(mc.field_1724.method_31477()));
            y.method_1852(String.valueOf(mc.field_1724.method_31478()));
            z.method_1852(String.valueOf(mc.field_1724.method_31479()));
        });
        btn("SAVE", px + pw - 68, py + ph - 24, 60, 16, this::save);
    }

    private void place(class_342 b, int bx, int by, int bw) {
        b.method_46421(bx); b.method_46419(by); b.method_25358(bw);
        method_37063(b);
    }

    private void save() {
        try {
            int ix = Integer.parseInt(x.method_1882()), iy = Integer.parseInt(y.method_1882()), iz = Integer.parseInt(z.method_1882());
            String cur = mc.field_1687 == null ? "overworld" : mc.field_1687.method_27983().toString();
            String dimStr = cur.replaceAll("overworld|the_nether|the_end", DIMS[dim]);
            String n = name.method_1882().isBlank() ? "Waypoint" : name.method_1882().trim();
            Config.get().waypointList.add(new Waypoint(n, ix, iy, iz, dimStr, Waypoints.PALETTE[colorIdx]));
            Config.save();
            method_25419();
        } catch (NumberFormatException e) {
            error = "X, Y and Z must be whole numbers.";
        }
    }

    @Override
    protected void draw(class_332 g, int mx, int my) {
        g.method_51433(field_22793, "Name", px + 8, py + 37, 0xFFFFFFFF, true);
        g.method_51433(field_22793, "X", px + 8, py + 57, 0xFFFFFFFF, true);
        g.method_51433(field_22793, "Y", px + 8, py + 77, 0xFFFFFFFF, true);
        g.method_51433(field_22793, "Z", px + 8, py + 97, 0xFFFFFFFF, true);
        g.method_51433(field_22793, "Dimension", px + 8, py + 117, 0xFFFFFFFF, true);
        int c = 0xFF000000 | Waypoints.PALETTE[colorIdx];
        g.method_25294(px + 60 + 162, py + 114, px + 60 + 176, py + 128, 0xFFFFFFFF);
        g.method_25294(px + 60 + 163, py + 115, px + 60 + 175, py + 127, c);
        if (!error.isEmpty()) g.method_51433(field_22793, error, px + 8, py + 140, 0xFFFF5555, true);
    }
}
