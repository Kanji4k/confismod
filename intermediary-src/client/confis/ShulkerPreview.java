package client.confis;

import java.util.List;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

/** Shift + hover a shulker box in any inventory: shows its 27 slots instead of the vanilla tooltip. */
public final class ShulkerPreview {
    private ShulkerPreview() {}

    public static boolean isShulker(ItemStack s) {
        return !s.isEmpty() && s.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock;
    }

    /** Shift held (either key). Read straight from GLFW so it does not depend on Screen helpers that change between versions. */
    private static boolean shiftDown() {
        long w = GLFW.glfwGetCurrentContext();
        return w != 0L && (GLFW.glfwGetKey(w, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(w, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS);
    }

    public static boolean wanted(ItemStack s) {
        Module m = Modules.byId("shulker");
        return m != null && m.cfg().enabled && shiftDown() && isShulker(s);
    }

    public static void draw(DrawContext g, MinecraftClient mc, ItemStack box, int mx, int my) {
        ContainerComponent comp = box.get(DataComponentTypes.CONTAINER);
        List<ItemStack> items = comp == null ? List.of() : comp.stream().toList();
        int w = 9 * 18 + 8, h = 3 * 18 + 8 + 12;
        int x = Math.max(4, Math.min(mx + 12, g.getScaledWindowWidth() - w - 4));
        int y = Math.max(4, Math.min(my + 12, g.getScaledWindowHeight() - h - 4));
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF8A5CC8);
        g.fill(x, y, x + w, y + h, 0xF0100010);
        g.drawText(mc.textRenderer, box.getName(), x + 4, y + 3, 0xFFFFFFFF, true);
        for (int i = 0; i < 27; i++) {
            int sx = x + 4 + (i % 9) * 18, sy = y + 15 + (i / 9) * 18;
            g.fill(sx, sy, sx + 18, sy + 18, 0x40FFFFFF);
            g.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF181818);
            if (i < items.size() && !items.get(i).isEmpty()) {
                g.drawItem(items.get(i), sx + 1, sy + 1);
                g.drawStackOverlay(mc.textRenderer, items.get(i), sx + 1, sy + 1);
            }
        }
        if (items.stream().allMatch(ItemStack::isEmpty))
            g.drawText(mc.textRenderer, "Empty", x + w - 4 - mc.textRenderer.getWidth("Empty"), y + 3, 0xFF9A9A9A, true);
    }
}
