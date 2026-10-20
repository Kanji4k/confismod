package client.confis;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1836;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_437;
import net.minecraft.class_5481;
import net.minecraft.class_7923;

/** Every item in the game: icon, name, id and its description (tooltip). Searchable. */
public class ItemGuideScreen extends ConfisScreen {
    private static List<class_1799> ALL;

    public static List<class_1799> allItems() {
        if (ALL == null) {
            ALL = new ArrayList<>();
            for (class_1792 it : class_7923.field_41178) if (it != class_1802.field_8162) ALL.add(new class_1799(it));
        }
        return ALL;
    }

    public static String idOf(class_1799 s) { return class_7923.field_41178.method_10221(s.method_7909()).method_12832(); }

    private static final int COLS = 12, ROWS = 8, SLOT = 18;

    private final class_342 search;
    private List<class_1799> shown = new ArrayList<>();
    private String query = "";
    private int page;
    private class_1799 selected;

    public ItemGuideScreen(class_437 parent) {
        super("Item Guide", parent);
        search = new class_342(mc.field_1772, 0, 0, 150, 14, class_2561.method_43470("Search"));
        search.method_47404(class_2561.method_43470("Search items..."));
        search.method_1880(32);
        search.method_1863(s -> { query = s.toLowerCase(Locale.ROOT).trim(); page = 0; markDirty(); });
        filter();
    }

    @Override protected int panelW() { return 490; }
    @Override protected int panelH() { return 280; }

    private void filter() {
        shown = new ArrayList<>();
        for (class_1799 s : allItems())
            if (query.isEmpty() || idOf(s).contains(query) || s.method_7964().getString().toLowerCase(Locale.ROOT).contains(query))
                shown.add(s);
    }

    @Override
    protected void build() {
        filter();
        search.method_46421(px + 8); search.method_46419(py + 30); search.method_25358(150);
        method_37063(search);
        setFocused(search);          // the screen is rebuilt after every letter: keep the box focused so you can keep typing
        search.setFocused(true);
        int per = COLS * ROWS;
        int pages = Math.max(1, (shown.size() + per - 1) / per);
        page = Math.min(page, pages - 1);
        for (int n = 0; n < per && page * per + n < shown.size(); n++) {
            class_1799 s = shown.get(page * per + n);
            btn("", px + 8 + (n % COLS) * SLOT, py + 52 + (n / COLS) * SLOT, SLOT, SLOT, () -> selected = s);
        }
        btn("<", px + 8, py + ph - 20, 18, 14, () -> { page = Math.max(0, page - 1); markDirty(); });
        btn(">", px + 8 + COLS * SLOT - 18, py + ph - 20, 18, 14, () -> { page = Math.min(pages - 1, page + 1); markDirty(); });
    }

    @Override
    protected void draw(class_332 g, int mx, int my) {
        int per = COLS * ROWS;
        int pages = Math.max(1, (shown.size() + per - 1) / per);
        for (int n = 0; n < per && page * per + n < shown.size(); n++)
            g.method_51427(shown.get(page * per + n), px + 9 + (n % COLS) * SLOT, py + 53 + (n / COLS) * SLOT);
        g.method_25300(field_22793, (page + 1) + "/" + pages + "  (" + shown.size() + " items)",
                px + 8 + COLS * SLOT / 2, py + ph - 17, 0xFFFFFFFF);

        if (shown.isEmpty()) g.method_51433(field_22793, "No items found.", px + 10, py + 56, 0xFF7A7A7A, true);

        int dx = px + 8 + COLS * SLOT + 10, dw = pw - (dx - px) - 8;
        g.method_25294(dx - 5, py + 28, dx - 4, py + ph - 8, 0xFF3A3A3A);
        if (selected == null) {
            g.method_51433(field_22793, "Click an item to read about it.", dx, py + 34, 0xFF7A7A7A, true);
            return;
        }
        g.method_51448().pushMatrix();
        g.method_51448().translate(dx, py + 32);
        g.method_51448().scale(3f, 3f);
        g.method_51427(selected, 0, 0);
        g.method_51448().popMatrix();
        g.method_51433(field_22793, selected.method_7964().getString(), dx + 56, py + 34, accent(), true);
        g.method_51433(field_22793, "minecraft:" + idOf(selected), dx + 56, py + 46, 0xFF9A9A9A, true);
        g.method_51433(field_22793, "Max stack: " + selected.method_7914()
                + (selected.method_7963() ? "   Durability: " + selected.method_7936() : ""),
                dx + 56, py + 58, 0xFFCCCCCC, true);

        int y = GuidePanel.draw(g, mc, selected, dx, py + 76, dw);
        List<class_2561> tip = selected.method_7950(class_1792.TooltipContext.field_51353, mc.field_1724, class_1836.field_41070);
        for (int i = 1; i < tip.size(); i++)                       // line 0 is the name, already shown
            for (class_5481 l : field_22793.method_1728(tip.get(i), dw)) {
                if (y > py + ph - 14) return;
                g.method_51430(field_22793, l, dx, y, 0xFFFFFFFF, true);
                y += 10;
            }
        if (tip.size() <= 1 && y <= py + ph - 14) g.method_51433(field_22793, "No extra description for this item.", dx, y, 0xFF7A7A7A, true);
    }
}
