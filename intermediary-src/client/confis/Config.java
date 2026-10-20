package client.confis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Config {
    /** Per-mod settings. Colors are ARGB ints. Position is a fraction of the screen. */
    public static class ModuleCfg {
        public boolean enabled = true;
        public double fx, fy;
        public int bg = 0x80000000;
        public int text = 0xFFFFFFFF;
        public int accent = 0xFFFFFFFF;
        public boolean showBg = true;
        public boolean favorite = false;
        /** Size multiplier for this HUD element (0.5 - 3.0). */
        public float scale = 1.0f;
        /** Inventory Hub: show the Quick Access Bar (the hotbar row at the bottom). */
        public boolean quickBar = true;
        /** Armor Status: durability text NUMBER / PERCENT / BOTH, and which side of the item it sits on. */
        public String armorMode = "NUMBER";
        public String armorSide = "RIGHT";
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("confis0oooooo.json");
    private static Config instance;

    public Map<String, ModuleCfg> modules = new LinkedHashMap<>();
    public int accent = 0xFFFFFFFF;
    public int panelBg = 0xF2050505;
    public boolean clock24h = true;
    public boolean nameBadge = true;
    public List<Waypoint> waypointList = new ArrayList<>();
    /** Friends list (player names) shown in the Friends menu. */
    public List<String> friends = new ArrayList<>();
    /** Dropped-item size: default for all items + per-item overrides keyed by item id (e.g. "diamond"). */
    public float dropScale = 1.0f;
    /** Hitbox switches by type key (crystal, exp, friendly, hostile, firework, pearl, windcharge) and nametags. */
    public boolean hitboxMaster = true;
    public Map<String, Boolean> hitboxes = new LinkedHashMap<>();
    public boolean nametags = true;
    /** Zoom key (GLFW key code, default C) and zoom strength. */
    public int zoomKey = org.lwjgl.glfw.GLFW.GLFW_KEY_C;
    public float zoomFactor = 4.0f;
    /** Freecam toggle key (GLFW key code, default F6) and fly speed multiplier. */
    public int freecamKey = org.lwjgl.glfw.GLFW.GLFW_KEY_F6;
    public float freecamSpeed = 1.0f;
    /** Brightness the player had before Fullbright switched it up (-1 = none stored). */
    public double savedGamma = -1;
    /** Tier tagger source: PVP (PvPTiers), MC (MCTiers) or BOTH. URL templates: %s = player name. */
    public String tierMode = "BOTH";
    public String tierUrlMc = "https://mctiers.com/api/search_profile/%s";
    public String tierUrlPvp = "https://pvptiers.com/api/search_profile/%s";
    /** Optional text file URL (one player name per line) listing other players that use this mod. */
    public String userListUrl = "";
    public Map<String, Float> itemScales = new LinkedHashMap<>();

    public static Config get() {
        if (instance == null) load();
        return instance;
    }

    public static void load() {
        try (Reader r = Files.newBufferedReader(FILE)) {
            instance = GSON.fromJson(r, Config.class);
        } catch (Exception ignored) {
        }
        if (instance == null) instance = new Config();
        if (instance.modules == null) instance.modules = new LinkedHashMap<>();
        if (instance.friends == null) instance.friends = new ArrayList<>();
        if (instance.waypointList == null) instance.waypointList = new ArrayList<>();
        if (instance.hitboxes == null) instance.hitboxes = new LinkedHashMap<>();
        if (instance.itemScales == null) instance.itemScales = new LinkedHashMap<>();
        if (instance.dropScale <= 0f) instance.dropScale = 1.0f;
        if (instance.freecamSpeed <= 0f) instance.freecamSpeed = 1.0f;
        for (ModuleCfg m : instance.modules.values()) {
            if (m.scale <= 0f) m.scale = 1.0f;
            if (m.armorMode == null) m.armorMode = "NUMBER";
            if (m.armorSide == null) m.armorSide = "RIGHT";
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(get(), w);
        } catch (Exception ignored) {
        }
    }
}
