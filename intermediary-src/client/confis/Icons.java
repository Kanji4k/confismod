package client.confis;

import net.minecraft.class_332;

/** Original 12x12 pixel icons (drawn from bitmaps, no image files needed). */
public final class Icons {
    private Icons() {}

    private static final String[] FPS = {"...######...", "..##....##..", ".#........#.", "#.......#..#", "#......#...#", "#.....#....#", "#....##....#", "#...##.....#", "#..........#", "............", ".##########.", "............"};
    private static final String[] PING = {"............", "............", ".........##.", ".........##.", ".........##.", "......##.##.", "......##.##.", "...##.##.##.", "...##.##.##.", "##.##.##.##.", "##.##.##.##.", "##.##.##.##."};
    private static final String[] TPS = {".......###..", "......###...", ".....###....", "....###.....", "...#######..", "..#######...", "......###...", ".....###....", ".....##.....", "....##......", "....#.......", "............"};
    private static final String[] CPS = {"...######...", "..#..#...#..", ".#...#....#.", ".#...#....#.", ".##########.", ".#........#.", ".#........#.", ".#........#.", ".#........#.", "..#......#..", "...######...", "............"};
    private static final String[] CLOCK = {"...######...", "..#......#..", ".#...#....#.", "#....#.....#", "#....#.....#", "#....#.....#", "#....###...#", "#..........#", ".#........#.", "..#......#..", "...######...", "............"};
    private static final String[] DAY = {".....##.....", ".#...##...#.", "..#......#..", "....####....", "...######...", "##.######.##", "##.######.##", "...######...", "....####....", "..#......#..", ".#...##...#.", ".....##....."};
    private static final String[] ARMOR = {"##..####..##", "####....####", "####....####", ".##########.", ".##########.", ".##########.", "..########..", "..########..", "..########..", "..###..###..", "..###..###..", "............"};
    private static final String[] KEYSTROKES = {"....###.....", "....#.#.....", "....###.....", "............", "###.###.###.", "#.#.#.#.#.#.", "###.###.###.", "............", "###########.", "#.........#.", "###########.", "............"};
    private static final String[] WAYPOINTS = {"....####....", "...######...", "..##....##..", "..#......#..", "..#..##..#..", "..#..##..#..", "..##....##..", "...#....#...", "....#..#....", ".....##.....", "............", "............"};
    private static final String[] EFFECTS = {"....####....", "...######...", "..########..", "..###..###..", "..########..", "...######...", "....####....", ".....##.....", "....####....", "...######...", "..########..", "............"};
    private static final String[] SCOREBOARD = {"############", "#..........#", "#.######...#", "#..........#", "#.####.....#", "#..........#", "#.#######..#", "#..........#", "#.###......#", "#..........#", "############", "............"};
    private static final String[] SEARCH = {".####..", "#....#.", "#....#.", "#....#.", ".####..", "....##.", ".....##"};
    private static final String[] REACH = {"............", "............", "#..........#", "#..........#", "#.#......#.#", "############", "#.#......#.#", "#..........#", "#..........#", "............", "............", "............"};
    private static final String[] RANGE = {"....####....", "..##....##..", ".#...##...#.", ".#..####..#.", "#..######..#", "#..######..#", ".#..####..#.", ".#...##...#.", "..##....##..", "....####....", "............", "............"};
    private static final String[] INVENTORY = {".###.###.###", ".#.#.#.#.#.#", ".###.###.###", "............", ".###.###.###", ".#.#.#.#.#.#", ".###.###.###", "............", ".###.###.###", ".#.#.#.#.#.#", ".###.###.###", "............"};
    private static final String[] TOTEMS = {"....####....", "...######...", "...#.##.#...", "...######...", "....####....", "..########..", ".##########.", "##.######.##", "...######...", "...##..##...", "..###..###..", "............"};
    private static final String[] POTIONS = {"....####....", ".....##.....", ".....##.....", "....####....", "...######...", "..########..", "..#.######..", "..########..", "..########..", "...######...", "............", "............"};
    private static final String[] SATURATION = {"......###...", ".....#####..", ".....#####..", ".....#####..", "....#####...", "...#####....", "..###.......", ".###........", "###.........", "##..........", "............", "............"};
    private static final String[] ZOOM = {"...######...", "..#......#..", ".#........#.", ".#........#.", ".#........#.", "..#......#..", "...######...", ".......###..", "........###.", ".........###", "..........##", "............"};
    private static final String[] TIERS = {"..########..", ".#.######.#.", ".#.######.#.", "..#.####.#..", "...######...", "....####....", ".....##.....", ".....##.....", "....####....", "...######...", "...######...", "............"};
    private static final String[] SHULKER = {"..########..", ".##########.", "############", "#..........#", "############", "#....##....#", "#....##....#", "#..........#", "#..........#", "############", "............", "............"};
    private static final String[] CROSSHAIR = {"............", ".....##.....", ".....##.....", "............", "###......###", "###......###", "............", ".....##.....", ".....##.....", "............", "............", "............"};
    private static final String[] FREECAM = {"............", "............", "...####.....", ".##########.", "#..........#", "#...####...#", "#..#....#..#", "#..#....#..#", "#...####...#", "#..........#", ".##########.", "............"};
    private static final String[] FULLBRIGHT = {"............", ".....##.....", "..#..##..#..", "...#....#...", "....####....", "##.######.##", "##.######.##", "....####....", "...#....#...", "..#..##..#..", ".....##.....", "............"};
    private static final String[] NOBLIND = {"............", "............", "...######...", ".###....###.", "##..####..##", "#..######..#", "#..######..#", "##..####..##", ".###....###.", "...######...", "............", "............"};
    private static final String[] HEART = {".##.##.", "#######", "#######", ".#####.", "..###..", "...#..."};
    private static final String[] GEAR = {"...#...", ".#####.", ".#...#.", "##.#.##", ".#...#.", ".#####.", "...#..."};

    private static String[] get(String id) {
        return switch (id) {
            case "fps" -> FPS;
            case "ping" -> PING;
            case "tps" -> TPS;
            case "cps" -> CPS;
            case "clock" -> CLOCK;
            case "day" -> DAY;
            case "armor" -> ARMOR;
            case "keystrokes" -> KEYSTROKES;
            case "waypoints" -> WAYPOINTS;
            case "effects" -> EFFECTS;
            case "scoreboard" -> SCOREBOARD;
            case "search" -> SEARCH;
            case "reach" -> REACH;
            case "range" -> RANGE;
            case "inventory" -> INVENTORY;
            case "totems" -> TOTEMS;
            case "potions" -> POTIONS;
            case "saturation" -> SATURATION;
            case "zoom" -> ZOOM;
            case "freecam" -> FREECAM;
            case "fullbright" -> FULLBRIGHT;
            case "noblind" -> NOBLIND;
            case "tiers" -> TIERS;
            case "shulker" -> SHULKER;
            case "crosshair" -> CROSSHAIR;
            case "heart" -> HEART;
            case "gear" -> GEAR;
            default -> null;
        };
    }

    /** Pixel-art tint used for the module cards in the menu. */
    public static int tint(String id) {
        return switch (id) {
            case "fps" -> 0xFF5AB4FF;
            case "ping" -> 0xFF55E055;
            case "tps" -> 0xFFFFD83A;
            case "cps" -> 0xFFE8707C;
            case "clock" -> 0xFFFFB400;
            case "day" -> 0xFFFFC21A;
            case "armor" -> 0xFF55E6E6;
            case "keystrokes" -> 0xFF7AB0FF;
            case "waypoints" -> 0xFFE5334A;
            case "effects" -> 0xFFB05CFF;
            case "scoreboard" -> 0xFF8DB8FF;
            case "reach", "range" -> 0xFFE8F0FF;
            case "saturation" -> 0xFFD0602E;
            case "inventory" -> 0xFFBFD8FF;
            case "totems" -> 0xFFE6A84A;
            case "potions" -> 0xFFB05CFF;
            case "zoom" -> 0xFF55D0FF;
            case "freecam" -> 0xFF8FD0FF;
            case "fullbright" -> 0xFFFFD83A;
            case "noblind" -> 0xFFBFE6FF;
            case "tiers" -> 0xFFFFB400;
            case "shulker" -> 0xFFB070FF;
            case "crosshair" -> 0xFF8FE8FF;
            default -> 0xFFFFFFFF;
        };
    }

    /** Draws the icon with its top-left at (x, y); each bitmap pixel becomes a scale x scale square. */
    public static void draw(class_332 g, String id, int x, int y, int scale, int color) {
        String[] rows = get(id);
        if (rows == null) return;
        for (int j = 0; j < rows.length; j++) {
            String r = rows[j];
            int i = 0;
            while (i < r.length()) {
                if (r.charAt(i) != '#') { i++; continue; }
                int k = i;
                while (k < r.length() && r.charAt(k) == '#') k++;
                g.method_25294(x + i * scale, y + j * scale, x + k * scale, y + (j + 1) * scale, color);
                i = k;
            }
        }
    }

    public static int width(String id) { String[] r = get(id); return r == null ? 0 : r[0].length(); }
    public static int height(String id) { String[] r = get(id); return r == null ? 0 : r.length; }
}
