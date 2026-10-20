package client.confis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_437;
import net.minecraft.class_640;
import net.minecraft.class_7532;

/** Searchable list of everyone in the server's player list: skin face, name, ping. */
public class PlayerSearchScreen extends ConfisScreen {
    private final class_342 search;
    private String query = "";
    private int page;
    private List<class_640> shown = new ArrayList<>();

    public PlayerSearchScreen(class_437 parent) {
        super("Online Players", parent);
        search = new class_342(mc.field_1772, 0, 0, 160, 14, class_2561.method_43470("Search"));
        search.method_47404(class_2561.method_43470("Search player name..."));
        search.method_1880(16);
        search.method_1863(s -> { query = s.toLowerCase(Locale.ROOT).trim(); page = 0; markDirty(); });
    }

    @Override protected int panelW() { return 340; }
    @Override protected int panelH() { return 260; }

    private int perPage() { return Math.max(1, (ph - 80) / 20); }

    private void filter() {
        shown = new ArrayList<>();
        if (mc.method_1562() != null)
            for (class_640 p : mc.method_1562().method_2880())
                if (query.isEmpty() || p.method_2966().name().toLowerCase(Locale.ROOT).contains(query)) shown.add(p);
        shown.sort(Comparator.comparing(p -> p.method_2966().name().toLowerCase(Locale.ROOT)));
    }

    @Override
    protected void build() {
        filter();
        search.method_46421(px + 8); search.method_46419(py + 30); search.method_25358(160);
        method_37063(search);
        setFocused(search);
        search.setFocused(true);
        int per = perPage();
        int pages = Math.max(1, (shown.size() + per - 1) / per);
        page = Math.min(page, pages - 1);
        for (int n = 0; n < per && page * per + n < shown.size(); n++) {
            String name = shown.get(page * per + n).method_2966().name();
            btn("MSG", px + pw - 44, py + 52 + n * 20, 36, 16, () -> mc.method_1507(new FriendsScreen(this, name)));
        }
        btn("<", px + pw - 78, py + ph - 20, 18, 14, () -> { page = Math.max(0, page - 1); markDirty(); });
        btn(">", px + pw - 28, py + ph - 20, 18, 14, () -> { page = Math.min(pages - 1, page + 1); markDirty(); });
    }

    @Override
    protected void draw(class_332 g, int mx, int my) {
        int per = perPage();
        int pages = Math.max(1, (shown.size() + per - 1) / per);
        g.method_51433(field_22793, shown.size() + " online", px + 176, py + 33, 0xFF9A9A9A, true);
        for (int n = 0; n < per && page * per + n < shown.size(); n++) {
            class_640 p = shown.get(page * per + n);
            int y = py + 52 + n * 20;
            g.method_25294(px + 8, y, px + pw - 48, y + 18, 0xFF0D0D0D);
            class_7532.method_52722(g, p.method_52810(), px + 11, y + 3, 12);
            g.method_51433(field_22793, p.method_2966().name(), px + 28, y + 5, 0xFFFFFFFF, true);
            String ping = p.method_2959() + " ms";
            g.method_51433(field_22793, ping, px + pw - 52 - field_22793.method_1727(ping), y + 5, 0xFF9A9A9A, true);
        }
        if (shown.isEmpty()) g.method_51433(field_22793, "No players found.", px + 10, py + 56, 0xFF7A7A7A, true);
        g.method_25300(field_22793, (page + 1) + "/" + pages, px + pw - 53, py + ph - 17, 0xFFFFFFFF);
    }
}
