package client.confis;

/** Estimates server TPS from the spacing of the server's time-update packets (sent every 20 ticks). */
public final class TpsTracker {
    private static long last = 0;
    private static double tps = -1;

    private TpsTracker() {}

    public static void onTimePacket() {
        // apply() runs once on the network thread and again on the game thread: count only the game thread
        if (!net.minecraft.client.MinecraftClient.getInstance().isOnThread()) return;
        long now = System.nanoTime();
        if (last != 0) {
            double seconds = (now - last) / 1.0e9;
            if (seconds > 0.05) {
                double inst = Math.min(20.0, 20.0 / seconds);
                tps = tps < 0 ? inst : tps * 0.6 + inst * 0.4;
            }
        }
        last = now;
    }

    public static void reset() { last = 0; tps = -1; }

    /** @return TPS in 0..20, or -1 if no data yet. */
    public static double get() { return tps; }
}
