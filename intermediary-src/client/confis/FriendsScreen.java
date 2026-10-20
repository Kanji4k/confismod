package client.confis;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_437;
import net.minecraft.class_5481;
import net.minecraft.class_640;
import net.minecraft.class_7532;

/**
 * Friends menu: type a player name to add a friend, pick a friend on the left and chat on the right.
 * Messages go through the server's /msg command, so they work on any server. Friends that use this
 * mod (listed in the userListUrl file) get a blue C.
 */
public class FriendsScreen extends ConfisScreen {
    private static String selected;
    private final class_342 input, addBox;
    private final KeyPoll enter = KeyPoll.enter();
    private int page;
    private boolean focusAdd;
    private String note = "";
    private long noteUntil;
    private List<String> contacts = new ArrayList<>();

    public FriendsScreen(class_437 parent, String preselect) {
        super("Friends", parent);
        if (preselect != null) selected = preselect;
        addBox = new class_342(mc.field_1772, 0, 0, 90, 14, class_2561.method_43470("Player name"));
        addBox.method_1880(16);
        addBox.method_47404(class_2561.method_43470("Add friend..."));
        input = new class_342(mc.field_1772, 0, 0, 100, 14, class_2561.method_43470("Message"));
        input.method_1880(200);
        input.method_47404(class_2561.method_43470("Type a message..."));
    }

    @Override protected int panelW() { return 460; }
    @Override protected int panelH() { return 270; }

    private static boolean isFriend(String name) {
        for (String f : Config.get().friends) if (f.equalsIgnoreCase(name)) return true;
        return false;
    }

    private void say(String s) { note = s; noteUntil = System.currentTimeMillis() + 3500; }

    private void addFriend() {
        String n = addBox.getText().trim();
        if (n.isEmpty()) return;
        if (!n.matches("[A-Za-z0-9_]{1,16}")) { say("Not a valid player name."); return; }
        if (mc.field_1724 != null && n.equalsIgnoreCase(mc.field_1724.method_7334().name())) { say("That is you."); return; }
        if (isFriend(n)) { say(n + " is already a friend."); return; }
        Config.get().friends.add(n);
        Config.save();
        selected = n;
        addBox.setText("");
        say("Added " + n + ".");
        markDirty();
    }

    private void removeFriend(String n) {
        Config.get().friends.removeIf(f -> f.equalsIgnoreCase(n));
        Config.save();
        markDirty();
    }

    private Set<String> online() {
        Set<String> out = new LinkedHashSet<>();
        if (mc.method_1562() != null)
            for (class_640 p : mc.method_1562().method_2880()) out.add(p.method_2966().name().toLowerCase(Locale.ROOT));
        return out;
    }

    private void refreshContacts() {
        Set<String> names = new LinkedHashSet<>(Config.get().friends);
        for (String n : MessengerLog.CHATS.keySet()) if (!isFriend(n)) names.add(n);
        contacts = new ArrayList<>(names);
    }

    private int top() { return py + 56; }
    private int perPage() { return Math.max(1, (ph - 56 - 28) / 18); }

    @Override
    protected void build() {
        refreshContacts();
        int per = perPage();
        int pages = Math.max(1, (contacts.size() + per - 1) / per);
        page = Math.min(page, pages - 1);
        for (int n = 0; n < per && page * per + n < contacts.size(); n++) {
            String name = contacts.get(page * per + n);
            btn("", px + 8, top() + n * 18, 130, 16, () -> {
                selected = name;
                MessengerLog.UNREAD.remove(name);
                markDirty();
            });
        }
        btn("<", px + 8, py + ph - 20, 18, 14, () -> { page = Math.max(0, page - 1); markDirty(); });
        btn(">", px + 120, py + ph - 20, 18, 14, () -> { page = Math.min(pages - 1, page + 1); markDirty(); });

        addBox.method_46421(px + 8); addBox.method_46419(py + 30); addBox.method_25358(92);
        method_37063(addBox);
        btn("ADD", px + 104, py + 29, 34, 16, this::addFriend);

        if (selected != null) {
            String sel = selected;
            if (isFriend(sel)) btn("REMOVE", px + pw - 62, py + 29, 54, 14, () -> removeFriend(sel));
            else btn("+ FRIEND", px + pw - 62, py + 29, 54, 14, () -> {
                Config.get().friends.add(sel); Config.save(); markDirty();
            });
        }

        input.method_46421(px + 146); input.method_46419(py + ph - 22); input.method_25358(pw - 146 - 62);
        method_37063(input);
        btn("SEND", px + pw - 52, py + ph - 23, 44, 16, this::send);

        // keep the cursor in the box you were typing in (the screen is rebuilt after changes)
        if (focusAdd || selected == null) { setFocused(addBox); addBox.setFocused(true); }
        else { setFocused(input); input.setFocused(true); }
    }

    private void send() {
        if (selected == null) return;
        MessengerLog.send(selected, input.method_1882());
        input.method_1852("");
    }

    @Override
    public void method_25393() {
        super.method_25393();
        if (addBox.isFocused()) focusAdd = true; else if (input.isFocused()) focusAdd = false;
        if (enter.pressed()) {
            if (addBox.isFocused()) addFriend();
            else if (input.isFocused()) send();
        }
    }

    @Override
    protected void draw(class_332 g, int mx, int my) {
        Set<String> on = online();
        int per = perPage();
        g.method_51433(field_22793, Config.get().friends.size() + " friends", px + 8, py + 47 - 0, 0xFF7A7A7A, true);
        if (System.currentTimeMillis() < noteUntil)
            g.method_51433(field_22793, note, px + 146, py + 8, 0xFFFFFF55, true);
        for (int n = 0; n < per && page * per + n < contacts.size(); n++) {
            String name = contacts.get(page * per + n);
            int y = top() + n * 18;
            boolean sel = name.equals(selected);
            boolean isOn = on.contains(name.toLowerCase(Locale.ROOT));
            if (sel) g.method_25294(px + 8, y, px + 138, y + 16, 0x40FFFFFF);
            g.method_25294(px + 11, y + 6, px + 15, y + 10, isOn ? 0xFF55FF55 : 0xFF555555);
            int unread = MessengerLog.UNREAD.getOrDefault(name, 0);
            String shown = name + (unread > 0 ? " (" + unread + ")" : "");
            int tx = px + 20;
            if (ModUsers.has(name)) {
                g.method_51433(field_22793, "C", tx, y + 4, 0xFF55FFFF, true);
                tx += 8;
            }
            g.method_51433(field_22793, shown + (isFriend(name) ? "" : " *"), tx, y + 4,
                    sel ? accent() : unread > 0 ? 0xFFFFFF55 : isOn ? 0xFFFFFFFF : 0xFF9A9A9A, true);
        }
        if (contacts.isEmpty())
            g.method_51433(field_22793, "Type a name above and press ADD.", px + 8, top() + 2, 0xFF7A7A7A, true);
        g.method_25294(px + 142, py + 28, px + 143, py + ph - 26, 0xFF3A3A3A);

        int x0 = px + 148, w = pw - 156 - 64;
        if (selected == null) {
            g.method_51433(field_22793, "Add or pick a friend on the left to chat.", x0, py + 34, 0xFF7A7A7A, true);
            return;
        }
        boolean isOn = on.contains(selected.toLowerCase(Locale.ROOT));
        g.method_51433(field_22793, "Chat with " + selected + (isOn ? "  (online)" : "  (offline)"), x0, py + 32, isOn ? accent() : 0xFF9A9A9A, true);
        if (!isOn) g.method_51433(field_22793, "Offline players can't get messages.", x0, py + 44, 0xFF7A7A7A, true);
        List<class_5481> lines = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();
        for (MessengerLog.Entry e : MessengerLog.CHATS.getOrDefault(selected, List.of()))
            for (class_5481 l : field_22793.method_1728(class_2561.method_43470((e.mine() ? "You: " : selected + ": ") + e.text()), pw - 156)) {
                lines.add(l);
                cols.add(e.mine() ? 0xFFB0B0B0 : 0xFFFFFFFF);
            }
        int top = py + 58, rows = Math.max(1, (ph - 58 - 30) / 10);
        for (int i = 0; i < rows && i < lines.size(); i++) {
            int idx = Math.max(0, lines.size() - rows) + i;
            if (idx >= lines.size()) break;
            g.method_51430(field_22793, lines.get(idx), x0, top + i * 10, cols.get(idx), true);
        }
    }
}
