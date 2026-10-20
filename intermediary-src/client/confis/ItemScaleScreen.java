package client.confis;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_437;

/** Resize dropped items: one default for all, plus a per-item override found with the search box. */
public class ItemScaleScreen extends ConfisScreen {
    private final class_342 search;
    private String query = "";
    private int page;
    private List<class_1799> shown = new ArrayList<>();

    public ItemScaleScreen(class_437 parent) {
        super("Item Drop Scale", parent);
        search = new class_342(mc.field_1772, 0, 0, 150, 14, class_2561.method_43470("Search"));
        search.method_47404(class_2561.method_43470("Search an item..."));
        search.method_1880(32);
        search.method_1863(s -> { query = s.toLowerCase(Locale.ROOT).trim(); page = 0; markDirty(); });
    }

    @Override protected int panelW() { return 400; }
    @Override protected int panelH() { return 270; }

    private int perPage() { return Math.max(1, (ph - 84) / 20); }

    private void filter() {
        shown = new ArrayList<>();
        for (class_1799 s : ItemGuideScreen.allItems())
            if (query.isEmpty() || ItemGuideScreen.idOf(s).contains(query)
                    || s.method_7964().getString().toLowerCase(Locale.ROOT).contains(query)) shown.add(s);
    }

    private void change(String id, int dir) {
        Config c = Config.get();
        c.itemScales.put(id, DropScale.step(DropScale.forId(id), dir));
        Config.save();
    }

    @Override
    protected void build() {
        filter();
        Config c = Config.get();
        btn("-", px + pw - 70, py + 30, 18, 14, () -> { c.dropScale = DropScale.step(c.dropScale, -1); Config.save(); });
        btn("+", px + pw - 48, py + 30, 18, 14, () -> { c.dropScale = DropScale.step(c.dropScale, 1); Config.save(); });
        search.method_46421(px + 8); search.method_46419(py + 52); search.method_25358(150);
        method_37063(search);
        setFocused(search);          // the screen is rebuilt after every letter: keep typing
        search.setFocused(true);

        int per = perPage();
        int pages = Math.max(1, (shown.size() + per - 1) / per);
        page = Math.min(page, pages - 1);
        for (int n = 0; n < per && page * per + n < shown.size(); n++) {
            String id = ItemGuideScreen.idOf(shown.get(page * per + n));
            int y = py + 72 + n * 20;
            btn("-", px + pw - 72, y, 18, 16, () -> change(id, -1));
            btn("+", px + pw - 52, y, 18, 16, () -> change(id, 1));
            btn("R", px + pw - 30, y, 18, 16, () -> { c.itemScales.remove(id); Config.save(); });
        }
        btn("<", px + pw - 78, py + ph - 20, 18, 14, () -> { page = Math.max(0, page - 1); markDirty(); });
        btn(">", px + pw - 28, py + ph - 20, 18, 14, () -> { page = Math.min(pages - 1, page + 1); markDirty(); });
        btn("Reset all", px + 8, py + ph - 20, 70, 14, () -> { c.itemScales.clear(); c.dropScale = 1f; Config.save(); });
    }

    @Override
    protected void draw(class_332 g, int mx, int my) {
        Config c = Config.get();
        g.method_51433(field_22793, "Default size for all drops: " + String.format("%.2fx", c.dropScale), px + 8, py + 33, 0xFFFFFFFF, true);
        int per = perPage();
        int pages = Math.max(1, (shown.size() + per - 1) / per);
        for (int n = 0; n < per && page * per + n < shown.size(); n++) {
            class_1799 s = shown.get(page * per + n);
            String id = ItemGuideScreen.idOf(s);
            int y = py + 72 + n * 20;
            g.method_25294(px + 8, y, px + pw - 76, y + 18, 0xFF0D0D0D);
            g.method_51427(s, px + 10, y + 1);
            g.method_51433(field_22793, s.method_7964().getString(), px + 32, y + 5, 0xFFFFFFFF, true);
            boolean custom = c.itemScales.containsKey(id);
            String t = String.format("%.2fx", DropScale.forId(id));
            g.method_51433(field_22793, t, px + pw - 80 - field_22793.method_1727(t), y + 5, custom ? accent() : 0xFF7A7A7A, true);
        }
        g.method_25300(field_22793, (page + 1) + "/" + pages, px + pw - 53, py + ph - 17, 0xFFFFFFFF);
    }
}
