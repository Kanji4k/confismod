package client.confis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.class_2561;
import net.minecraft.class_2588;
import net.minecraft.class_310;

/**
 * Private messages between players. Sending uses the server's /msg command, so it works with any
 * player on the server (the other player does not need this client to read it, but if they do they
 * get it in the Messenger too).
 */
public final class MessengerLog {
    public record Entry(boolean mine, String text) {}

    public static final Map<String, List<Entry>> CHATS = new LinkedHashMap<>();
    public static final Map<String, Integer> UNREAD = new LinkedHashMap<>();

    private MessengerLog() {}

    public static void register() {
        ClientReceiveMessageEvents.GAME.register((msg, overlay) -> { if (!overlay) onMessage(msg); });
    }

    private static final java.util.regex.Pattern[] PLAIN = {
            java.util.regex.Pattern.compile("^\\[(\\w{1,16}) (?:->|\u2192|\u00bb) (?:me|you)\\]\\s*(.*)$"),
            java.util.regex.Pattern.compile("^(\\w{1,16}) whispers to you: (.*)$"),
            java.util.regex.Pattern.compile("^(?:\\[)?From (\\w{1,16})(?:\\])?:? (.*)$"),
            java.util.regex.Pattern.compile("^\\[(\\w{1,16}) whispers\\]:? (.*)$"),
    };

    private static void incoming(String from, String text) {
        add(from, false, text);
        UNREAD.merge(from, 1, Integer::sum);
        class_310 mc = class_310.method_1551();
        boolean friend = false;
        for (String f : Config.get().friends) if (f.equalsIgnoreCase(from)) friend = true;
        if (friend && mc.inGameHud != null && !(mc.currentScreen instanceof FriendsScreen))
            mc.inGameHud.setOverlayMessage(net.minecraft.text.Text.literal("Friend " + from + ": " + text), false);
    }

    private static void onMessage(class_2561 msg) {
        if (msg.method_10851() instanceof class_2588 tc
                && tc.method_11022().equals("commands.message.display.incoming")
                && tc.method_11023().length >= 2) {
            String from = str(tc.method_11023()[0]);
            String text = str(tc.method_11023()[1]);
            incoming(from, text);
            return;
        }
        String plain = msg.getString();
        for (java.util.regex.Pattern p : PLAIN) {
            java.util.regex.Matcher m = p.matcher(plain);
            if (m.matches()) { incoming(m.group(1), m.group(2)); return; }
        }
    }

    private static String str(Object o) { return o instanceof class_2561 c ? c.getString() : String.valueOf(o); }

    public static void add(String who, boolean mine, String text) {
        CHATS.computeIfAbsent(who, k -> new ArrayList<>()).add(new Entry(mine, text));
    }

    public static void send(String who, String text) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || who == null || text.isBlank()) return;
        mc.field_1724.field_3944.method_45730("msg " + who + " " + text);
        add(who, true, text);
    }
}
