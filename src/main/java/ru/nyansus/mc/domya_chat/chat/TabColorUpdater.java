package ru.nyansus.mc.domya_chat.chat;

import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.nyansus.mc.domya_chat.color.PlayerColorManager;

public class TabColorUpdater implements Listener {

    private final PlayerColorManager colorManager;
    private final Supplier<Boolean> enabledSupplier;
    private final Supplier<int[]> pingThresholdsSupplier;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final boolean hasPapi;

    public TabColorUpdater(PlayerColorManager colorManager,
                           Supplier<Boolean> enabledSupplier,
                           Supplier<int[]> pingThresholdsSupplier) {
        this.colorManager = colorManager;
        this.enabledSupplier = enabledSupplier;
        this.pingThresholdsSupplier = pingThresholdsSupplier;
        this.hasPapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public void startUpdateTask(JavaPlugin plugin, long intervalTicks) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!enabledSupplier.get()) {
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateTabName(player);
            }
        }, 100L, intervalTicks);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updateTabName(event.getPlayer());
    }

    public void updateTabName(Player player) {
        if (!enabledSupplier.get()) {
            player.playerListName(null);
            return;
        }
        StringBuilder prefix = new StringBuilder();
        String karmaTitle = resolveKarmaTitle(player);
        if (!karmaTitle.isEmpty()) {
            prefix.append(karmaTitle).append(" ");
        }
        Component result = miniMessage.deserialize(prefix.toString())
                .append(colorManager.renderGradient(player.getName(), player));
        StringBuilder suffix = new StringBuilder();
        String karma = resolveKarma(player);
        if (!karma.isEmpty()) {
            suffix.append(" <dark_gray>| ").append(karmaColor(karma))
                    .append(karma);
        }
        suffix.append(" <dark_gray>| ").append(pingColor(player.getPing()))
                .append(player.getPing()).append("ms");
        player.playerListName(result.append(miniMessage.deserialize(suffix.toString())));
    }

    private String resolveKarmaTitle(Player player) {
        if (!hasPapi) {
            return "";
        }
        return me.clip.placeholderapi.PlaceholderAPI
                .setPlaceholders(player, "%domya_title_colored%");
    }

    private String resolveKarma(Player player) {
        if (!hasPapi) {
            return "";
        }
        return me.clip.placeholderapi.PlaceholderAPI
                .setPlaceholders(player, "%domya_karma%");
    }

    private String karmaColor(String karma) {
        try {
            int value = Integer.parseInt(karma);
            if (value > 0) return "<green>";
            if (value < 0) return "<red>";
        } catch (NumberFormatException ignored) {
            // ignore
        }
        return "<gray>";
    }

    private String pingColor(int ping) {
        int[] thresholds = pingThresholdsSupplier.get();
        if (ping < thresholds[0]) return "<green>";
        if (ping < thresholds[1]) return "<yellow>";
        return "<red>";
    }
}
