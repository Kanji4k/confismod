package client.confis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** Right-hand part of the Item Guide: crafting recipe grid + where to find the item. */
public final class GuidePanel {
    private static final Map<String, ItemStack> CACHE = new HashMap<>();

    private GuidePanel() {}

    private static ItemStack stack(String id) {
        return CACHE.computeIfAbsent(id, k -> new ItemStack(Registries.ITEM.get(Identifier.of("minecraft", k))));
    }

    private static void slot(DrawContext g, int x, int y) {
        g.fill(x, y, x + 18, y + 18, 0xFF3A3A3A);
        g.fill(x + 1, y + 1, x + 17, y + 17, 0xFF0D0D0D);
    }

    private static int wrap(DrawContext g, TextRenderer f, String s, int x, int y, int w, int color, int maxLines) {
        List<OrderedText> lines = f.wrapLines(Text.literal(s), w);
        for (int i = 0; i < Math.min(maxLines, lines.size()); i++) {
            g.drawText(f, lines.get(i), x, y, color, true);
            y += 10;
        }
        return y;
    }

    /** Draws the panel starting at y and returns the y where the next block can start. */
    public static int draw(DrawContext g, MinecraftClient mc, ItemStack selected, int x, int y, int w) {
        TextRenderer f = mc.textRenderer;
        String id = ItemGuideScreen.idOf(selected);

        g.drawText(f, "How to craft:", x, y, 0xFFFFAA00, true);
        y += 11;
        GuideData.Recipe r = GuideData.recipe(id);
        if (r == null) {
            y = wrap(g, f, "No crafting recipe in the guide (not craftable, or not added yet).", x, y, w, 0xFF9A9A9A, 2);
        } else {
            int cols = r.cols();
            int rows = (r.cells().size() + cols - 1) / cols;
            for (int i = 0; i < r.cells().size(); i++) {
                int sx = x + (i % cols) * 18, sy = y + (i / cols) * 18;
                slot(g, sx, sy);
                String cell = r.cells().get(i);
                if (!cell.isEmpty()) g.drawItem(stack(cell), sx + 1, sy + 1);
            }
            int gh = rows * 18;
            int ax = x + cols * 18 + 8;
            g.drawText(f, "->", ax, y + (gh - 8) / 2, 0xFFFFFFFF, true);
            int rx = ax + 20, ry = y + (gh - 18) / 2;
            slot(g, rx, ry);
            g.drawItem(stack(r.out()), rx + 1, ry + 1);
            if (r.count() > 1) g.drawText(f, "x" + r.count(), rx + 22, ry + 5, 0xFFFFFFFF, true);
            y += gh + 3;
            g.drawText(f, "At: " + r.station(), x, y, 0xFF9A9A9A, true);
            y += 12;
        }

        g.drawText(f, "Where to find:", x, y, 0xFFFFAA00, true);
        y += 11;
        y = wrap(g, f, GuideData.where(id), x, y, w, 0xFFFFFFFF, 4);
        return y + 6;
    }
}
