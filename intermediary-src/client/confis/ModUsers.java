package client.confis;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Names of other players that use this mod. Minecraft servers give no way to detect that on their own, so the
 * list is read from a plain text file (one player name per line) at the URL set in userListUrl in the config.
 */
public final class ModUsers {
    private static final Set<String> USERS = ConcurrentHashMap.newKeySet();

    private ModUsers() {}

    public static boolean has(String name) { return USERS.contains(name.toLowerCase()); }

    public static void refresh() {
        String url = Config.get().userListUrl;
        if (url == null || url.isBlank()) return;
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(8)).GET().build();
            HttpClient.newHttpClient().sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> {
                if (res.statusCode() != 200) return;
                USERS.clear();
                for (String line : res.body().split("\\R")) {
                    String s = line.trim().toLowerCase();
                    if (!s.isEmpty() && !s.startsWith("#")) USERS.add(s);
                }
            });
        } catch (Exception ignored) {
        }
    }
}
