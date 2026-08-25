package ru.nyansus.mc.ethos_chat.afk;

import java.util.UUID;
import java.util.function.LongSupplier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ru.nyansus.mc.ethos_chat.Messages;

public class AfkManager implements Listener {

    private static final long CHECK_INTERVAL_TICKS = 20L;

    private final JavaPlugin plugin;
    private final Messages messages;
    private final LongSupplier timeoutSecondsSupplier;
    private final AfkTracker tracker = new AfkTracker();
    private BukkitTask checkTask;

    public AfkManager(JavaPlugin plugin, Messages messages,
                      LongSupplier timeoutSecondsSupplier) {
        this.plugin = plugin;
        this.messages = messages;
        this.timeoutSecondsSupplier = timeoutSecondsSupplier;
    }

    public void start() {
        long now = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            tracker.join(player.getUniqueId(), now);
        }
        checkTask = Bukkit.getScheduler().runTaskTimer(
                plugin, this::checkIdlePlayers,
                CHECK_INTERVAL_TICKS, CHECK_INTERVAL_TICKS);
    }

    public void stop() {
        if (checkTask != null) {
            checkTask.cancel();
            checkTask = null;
        }
        tracker.clear();
    }

    public void toggle(Player player) {
        boolean afk = tracker.toggle(player.getUniqueId(), System.currentTimeMillis());
        broadcast(player, afk ? "afk.away" : "afk.back");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        tracker.join(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        tracker.quit(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        if (to == null || event.getFrom().equals(to)) {
            return;
        }
        Player player = event.getPlayer();
        if (tracker.recordActivity(player.getUniqueId(), System.currentTimeMillis())) {
            broadcast(player, "afk.back");
        }
    }

    private void checkIdlePlayers() {
        long timeoutSeconds = timeoutSecondsSupplier.getAsLong();
        if (timeoutSeconds <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        long timeoutMillis = timeoutSeconds * 1000L;
        for (UUID uuid : tracker.markIdle(now, timeoutMillis)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                broadcast(player, "afk.away");
            }
        }
    }

    private void broadcast(Player player, String messageKey) {
        for (Player recipient : Bukkit.getOnlinePlayers()) {
            messages.send(recipient, messageKey, "{player}", player.getName());
        }
    }
}
