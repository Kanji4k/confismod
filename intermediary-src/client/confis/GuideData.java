package client.confis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Built-in crafting recipes and "where to find" notes for the Item Guide (item ids without "minecraft:"). */
public final class GuideData {
    /** cells are item ids ("" = empty), laid out left to right in rows of `cols`. */
    public record Recipe(String out, int count, List<String> cells, int cols, String station) {}

    private static final Map<String, Recipe> RECIPES = new HashMap<>();
    private static final Map<String, String> WHERE = new HashMap<>();

    private GuideData() {}

    public static Recipe recipe(String id) { return RECIPES.get(id); }

    public static String where(String id) {
        String w = WHERE.get(id);
        if (w != null) return w;
        if (id.endsWith("_spawn_egg")) return "Creative mode only.";
        if (id.startsWith("music_disc_")) return "Rare loot chests, or a creeper killed by a skeleton.";
        if (id.endsWith("_log") || id.endsWith("_stem") || id.endsWith("_wood") || id.endsWith("_hyphae")) return "Chop the matching tree (Nether stems come from huge fungi).";
        if (id.endsWith("_planks")) return "Craft from the matching log or stem.";
        if (id.endsWith("_ore")) return "Mine it underground (deepslate versions are below Y=0). Needs a good enough pickaxe.";
        if (id.endsWith("_wool")) return "Shear a sheep of that color, or craft it from string.";
        if (id.endsWith("_leaves") || id.endsWith("_sapling")) return "Break the leaves of the matching tree.";
        if (id.endsWith("_smithing_template")) return "Structure chests (see the wiki for which structure).";
        if (id.endsWith("_pottery_sherd")) return "Brush suspicious sand / gravel in archaeology sites.";
        if (RECIPES.containsKey(id)) return "Craftable - see the recipe above.";
        return "No location notes for this item yet.";
    }

    private static void put(String id, String text) { WHERE.put(id, text); }

    private static void shaped(String out, int count, String pattern, String... legend) {
        Map<Character, String> key = new HashMap<>();
        for (String l : legend) key.put(l.charAt(0), l.substring(2));
        String[] rows = pattern.split("\\|", -1);
        int cols = 0;
        for (String r : rows) cols = Math.max(cols, r.length());
        List<String> cells = new ArrayList<>();
        for (String r : rows)
            for (int i = 0; i < cols; i++) cells.add(i < r.length() && r.charAt(i) != ' ' ? key.get(r.charAt(i)) : "");
        RECIPES.put(out, new Recipe(out, count, cells, cols, "Crafting Table"));
    }

    private static void shapeless(String out, int count, String station, String... items) {
        RECIPES.put(out, new Recipe(out, count, Arrays.asList(items), Math.min(3, items.length), station));
    }

    static {
        // ---- tools & weapons (all materials share one shape)
        String[][] mats = {{"wooden", "oak_planks"}, {"stone", "cobblestone"}, {"iron", "iron_ingot"}, {"golden", "gold_ingot"}, {"diamond", "diamond"}};
        for (String[] m : mats) {
            String k = "M=" + m[1];
            shaped(m[0] + "_pickaxe", 1, "MMM| S | S ", k, "S=stick");
            shaped(m[0] + "_axe", 1, "MM |MS | S ", k, "S=stick");
            shaped(m[0] + "_shovel", 1, "M|S|S", k, "S=stick");
            shaped(m[0] + "_hoe", 1, "MM| S| S", k, "S=stick");
            shaped(m[0] + "_sword", 1, "M|M|S", k, "S=stick");
        }
        // ---- armor
        String[][] arm = {{"leather", "leather"}, {"iron", "iron_ingot"}, {"golden", "gold_ingot"}, {"diamond", "diamond"}};
        for (String[] m : arm) {
            String k = "M=" + m[1];
            shaped(m[0] + "_helmet", 1, "MMM|M M", k);
            shaped(m[0] + "_chestplate", 1, "M M|MMM|MMM", k);
            shaped(m[0] + "_leggings", 1, "MMM|M M|M M", k);
            shaped(m[0] + "_boots", 1, "M M|M M", k);
        }
        // ---- netherite upgrade (smithing table)
        for (String p : new String[]{"helmet", "chestplate", "leggings", "boots", "pickaxe", "axe", "shovel", "hoe", "sword"})
            RECIPES.put("netherite_" + p, new Recipe("netherite_" + p, 1,
                    List.of("netherite_upgrade_smithing_template", "diamond_" + p, "netherite_ingot"), 3, "Smithing Table"));
        shapeless("netherite_ingot", 1, "Crafting Table", "netherite_scrap", "netherite_scrap", "netherite_scrap",
                "netherite_scrap", "gold_ingot", "gold_ingot", "gold_ingot", "gold_ingot");
        // ---- planks and storage blocks
        for (String w : new String[]{"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "cherry", "mangrove"})
            shapeless(w + "_planks", 4, "Crafting Table", w + "_log");
        String[][] blocks = {{"iron_ingot", "iron_block"}, {"gold_ingot", "gold_block"}, {"diamond", "diamond_block"},
                {"emerald", "emerald_block"}, {"redstone", "redstone_block"}, {"lapis_lazuli", "lapis_block"},
                {"coal", "coal_block"}, {"copper_ingot", "copper_block"}, {"netherite_ingot", "netherite_block"}};
        for (String[] b : blocks) {
            shaped(b[1], 1, "MMM|MMM|MMM", "M=" + b[0]);
            String[] nine = new String[1];
            nine[0] = b[1];
            shapeless(b[0], 9, "Crafting Table", nine);
        }
        // ---- everyday items
        shaped("crafting_table", 1, "PP|PP", "P=oak_planks");
        shaped("stick", 4, "P|P", "P=oak_planks");
        shaped("chest", 1, "PPP|P P|PPP", "P=oak_planks");
        shaped("furnace", 1, "CCC|C C|CCC", "C=cobblestone");
        shaped("torch", 4, "C|S", "C=coal", "S=stick");
        shaped("ladder", 3, "S S|SSS|S S", "S=stick");
        shaped("bow", 1, " ST|S T| ST", "S=stick", "T=string");
        shaped("arrow", 4, "F|S|E", "F=flint", "S=stick", "E=feather");
        shaped("shield", 1, "PIP|PPP| P ", "P=oak_planks", "I=iron_ingot");
        shaped("bucket", 1, "I I| I ", "I=iron_ingot");
        shaped("shears", 1, " I|I ", "I=iron_ingot");
        shapeless("flint_and_steel", 1, "Crafting Table", "iron_ingot", "flint");
        shaped("compass", 1, " I |IRI| I ", "I=iron_ingot", "R=redstone");
        shaped("clock", 1, " G |GRG| G ", "G=gold_ingot", "R=redstone");
        shaped("tnt", 1, "GSG|SGS|GSG", "G=gunpowder", "S=sand");
        shaped("piston", 1, "PPP|CIC|CRC", "P=oak_planks", "C=cobblestone", "I=iron_ingot", "R=redstone");
        shaped("sticky_piston", 1, "S|P", "S=slime_ball", "P=piston");
        shaped("hopper", 1, "I I|ICI| I ", "I=iron_ingot", "C=chest");
        shaped("bookshelf", 1, "PPP|BBB|PPP", "P=oak_planks", "B=book");
        shapeless("book", 1, "Crafting Table", "paper", "paper", "paper", "leather");
        shaped("paper", 3, "SSS", "S=sugar_cane");
        shaped("enchanting_table", 1, " B |DOD|OOO", "B=book", "D=diamond", "O=obsidian");
        shaped("anvil", 1, "BBB| I |III", "B=iron_block", "I=iron_ingot");
        shaped("brewing_stand", 1, " R |CCC", "R=blaze_rod", "C=cobblestone");
        shaped("cauldron", 1, "I I|I I|III", "I=iron_ingot");
        shaped("lever", 1, "S|C", "S=stick", "C=cobblestone");
        shaped("redstone_torch", 1, "R|S", "R=redstone", "S=stick");
        shaped("minecart", 1, "I I|III", "I=iron_ingot");
        shaped("rail", 16, "I I|ISI|I I", "I=iron_ingot", "S=stick");
        shaped("powered_rail", 6, "G G|GSG|GRG", "G=gold_ingot", "S=stick", "R=redstone");
        shaped("lantern", 1, "NNN|NTN|NNN", "N=iron_nugget", "T=torch");
        shaped("campfire", 1, " S |SCS|LLL", "S=stick", "C=coal", "L=oak_log");
        shaped("bread", 1, "WWW", "W=wheat");
        shaped("cake", 1, "MMM|SES|WWW", "M=milk_bucket", "S=sugar", "E=egg", "W=wheat");
        shaped("glass_bottle", 3, "G G| G ", "G=glass");
        shaped("bowl", 4, "P P| P ", "P=oak_planks");
        shapeless("ender_eye", 1, "Crafting Table", "blaze_powder", "ender_pearl");
        shapeless("blaze_powder", 2, "Crafting Table", "blaze_rod");
        shaped("end_crystal", 1, "GGG|GEG|GTG", "G=glass", "E=ender_eye", "T=ghast_tear");
        shaped("respawn_anchor", 1, "OOO|GGG|OOO", "O=crying_obsidian", "G=glowstone");
        shaped("crossbow", 1, "SIS|THT| S ", "S=stick", "I=iron_ingot", "T=string", "H=tripwire_hook");
        shaped("fishing_rod", 1, "  S| ST|S T", "S=stick", "T=string");
        shaped("beacon", 1, "GGG|GNG|OOO", "G=glass", "N=nether_star", "O=obsidian");
        shaped("golden_apple", 1, "GGG|GAG|GGG", "G=gold_ingot", "A=apple");
        shaped("ender_chest", 1, "OOO|OEO|OOO", "O=obsidian", "E=ender_eye");
        shaped("dispenser", 1, "CCC|CBC|CRC", "C=cobblestone", "B=bow", "R=redstone");
        shaped("dropper", 1, "CCC|C C|CRC", "C=cobblestone", "R=redstone");
        shaped("observer", 1, "CCC|RRQ|CCC", "C=cobblestone", "R=redstone", "Q=quartz");
        shaped("jukebox", 1, "PPP|PDP|PPP", "P=oak_planks", "D=diamond");
        shaped("note_block", 1, "PPP|PRP|PPP", "P=oak_planks", "R=redstone");

        // ---- where to find
        put("diamond", "Diamond ore, mostly below Y=16 (best near Y=-59). Also in some structure chests.");
        put("iron_ingot", "Smelt raw iron (iron ore, best near Y=16 or in mountains). Iron golems also drop it.");
        put("gold_ingot", "Smelt raw gold (gold ore below Y=32, most in badlands). Nether gold ore gives nuggets.");
        put("copper_ingot", "Smelt raw copper (copper ore, common around Y=48; dripstone caves).");
        put("coal", "Coal ore at any height, most common around Y=96.");
        put("lapis_lazuli", "Lapis ore around Y=0; also from cleric villagers.");
        put("redstone", "Redstone ore near the bottom of the world; witches also drop it.");
        put("emerald", "Emerald ore in mountain biomes, or trade with villagers.");
        put("netherite_scrap", "Smelt ancient debris.");
        put("ancient_debris", "The Nether, mostly Y=8 to 22 (best around Y=15). Mine with a diamond pickaxe.");
        put("quartz", "Nether quartz ore in the Nether.");
        put("blaze_rod", "Blazes, found in Nether fortresses.");
        put("ender_pearl", "Dropped by Endermen (most common in warped forests and the End).");
        put("gunpowder", "Creepers, witches and ghasts drop it.");
        put("bone", "Skeletons drop it.");
        put("string", "Spiders, cobwebs (with a sword or shears) and fishing.");
        put("slime_ball", "Slimes (swamps at night, and slime chunks underground).");
        put("totem_of_undying", "Dropped by evokers (woodland mansions and raids).");
        put("elytra", "Item frame on an End ship in an End City.");
        put("shulker_shell", "Shulkers in End Cities.");
        put("nether_star", "Dropped by the Wither.");
        put("trident", "Rarely dropped by drowned.");
        put("heart_of_the_sea", "Buried treasure chests.");
        put("nautilus_shell", "Rarely from drowned, or by fishing.");
        put("sponge", "Ocean monuments (wet sponge - dry it in a furnace).");
        put("enchanted_golden_apple", "Cannot be crafted. Rare chest loot in various structures.");
        put("obsidian", "Water flowing onto lava source blocks. Needs a diamond or netherite pickaxe.");
        put("cobblestone", "Mine stone with any pickaxe.");
        put("stone", "Smelt cobblestone, or mine stone with Silk Touch.");
        put("glowstone_dust", "Break glowstone clusters on Nether ceilings; witches drop it.");
        put("soul_sand", "Soul sand valleys in the Nether.");
        put("nether_wart", "Nether fortresses.");
        put("ghast_tear", "Ghasts in the Nether.");
        put("dragon_egg", "Appears after the first Ender Dragon kill.");
        put("echo_shard", "Chests in ancient cities.");
        put("wither_skeleton_skull", "Rarely dropped by wither skeletons in Nether fortresses.");
        put("saddle", "Loot chests, fishing and some trades.");
        put("name_tag", "Loot chests, fishing and some trades.");
        put("apple", "Oak leaves can drop it when they decay.");
        put("sand", "Beaches, deserts and riverbeds.");
        put("gravel", "Underground and riverbeds.");
        put("flint", "Break gravel.");
        put("feather", "Chickens drop it.");
        put("leather", "Cows, horses and llamas drop it.");
        put("ender_chest", "Craft it (needs an eye of ender and obsidian).");
    }
}
