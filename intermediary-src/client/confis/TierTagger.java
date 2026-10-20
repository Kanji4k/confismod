package client.confis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Looks a player's tier up on MCTiers and/or PvPTiers (async, cached). The two URL templates live in the
 * config file (tierUrlMc / tierUrlPvp, %s = player name) so they can be fixed without rebuilding.
 */
public final class TierTagger {
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private static final Map<String, String> MC = new ConcurrentHashMap<>();
    private static final Map<String, String> PVP = new ConcurrentHashMap<>();
    private static final Set<String> PENDING = ConcurrentHashMap.newKeySet();

    private TierTagger() {}

    public static void clear() { MC.clear(); PVP.clear(); }

    /** Text such as "MC HT3 | PvP LT4", or "" while loading / unranked. */
    public static String tag(String name) {
        Config cfg = Config.get();
        String mode = cfg.tierMode == null ? "BOTH" : cfg.tierMode;
        String mc = mode.equals("PVP") ? "" : lookup(MC, "mc", cfg.tierUrlMc, name);
        String pvp = mode.equals("MC") ? "" : lookup(PVP, "pvp", cfg.tierUrlPvp, name);
        StringBuilder sb = new StringBuilder();
        if (!mc.isEmpty()) sb.append("MC ").append(mc);
        if (!pvp.isEmpty()) sb.append(sb.length() > 0 ? " | " : "").append("PvP ").append(pvp);
        return sb.toString();
    }

    private static String lookup(Map<String, String> cache, String src, String template, String name) {
        String key = name.toLowerCase();
        String hit = cache.get(key);
        if (hit != null) return hit;
        if (template != null && !template.isBlank() && PENDING.add(src + ":" + key)) {
            HttpRequest req = HttpRequest.newBuilder(URI.create(String.format(template, name)))
                    .timeout(Duration.ofSeconds(8)).header("Accept", "application/json").GET().build();
            HTTP.sendAsync(req, HttpResponse.BodyHandlers.ofString()).whenComplete((res, err) -> {
                String out = "";
                if (err == null && res.statusCode() == 200) out = parse(res.body());
                cache.put(key, out);
                PENDING.remove(src + ":" + key);
            });
        }
        return "";
    }

    /** Finds the best (lowest tier, high before low) ranking in the profile JSON. */
    static String parse(String json) {
        try {
            JsonElement root = JsonParser.parseString(json);
            if (!root.isJsonObject()) return "";
            JsonObject o = root.getAsJsonObject();
            JsonObject r = o.has("rankings") && o.get("rankings").isJsonObject() ? o.getAsJsonObject("rankings") : o;
            int bestTier = 99, bestPos = 1;
            for (Map.Entry<String, JsonElement> e : r.entrySet()) {
                if (!e.getValue().isJsonObject()) continue;
                JsonObject m = e.getValue().getAsJsonObject();
                if (!m.has("tier")) continue;
                if (m.has("retired") && m.get("retired").getAsBoolean()) continue;
                int t = m.get("tier").getAsInt();
                int p = m.has("pos") ? m.get("pos").getAsInt() : 1;
                if (t < bestTier || (t == bestTier && p < bestPos)) { bestTier = t; bestPos = p; }
            }
            return bestTier == 99 ? "" : (bestPos == 0 ? "HT" : "LT") + bestTier;
        } catch (Exception ex) {
            return "";
        }
    }
}
