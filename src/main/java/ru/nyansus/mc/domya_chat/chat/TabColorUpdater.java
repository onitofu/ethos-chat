package ru.nyansus.mc.domya_chat.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    private static final int COL_PADDING = 8;

    private final PlayerColorManager colorManager;
    private final Supplier<Boolean> enabledSupplier;
    private final Supplier<int[]> pingThresholdsSupplier;
    private final Supplier<String> titlePlaceholderSupplier;
    private final Supplier<String> karmaPlaceholderSupplier;
    private final Supplier<String> titleWrapSupplier;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final boolean hasPapi;
    private JavaPlugin plugin;

    public TabColorUpdater(PlayerColorManager colorManager,
                           Supplier<Boolean> enabledSupplier,
                           Supplier<int[]> pingThresholdsSupplier,
                           Supplier<String> titlePlaceholderSupplier,
                           Supplier<String> karmaPlaceholderSupplier,
                           Supplier<String> titleWrapSupplier) {
        this.colorManager = colorManager;
        this.enabledSupplier = enabledSupplier;
        this.pingThresholdsSupplier = pingThresholdsSupplier;
        this.titlePlaceholderSupplier = titlePlaceholderSupplier;
        this.karmaPlaceholderSupplier = karmaPlaceholderSupplier;
        this.titleWrapSupplier = titleWrapSupplier;
        this.hasPapi = Bukkit.getPluginManager()
                .getPlugin("PlaceholderAPI") != null;
    }

    public void startUpdateTask(JavaPlugin pluginInstance, long intervalTicks) {
        this.plugin = pluginInstance;
        Bukkit.getScheduler().runTaskTimer(pluginInstance, () -> {
            if (!enabledSupplier.get()) {
                return;
            }
            updateAll();
        }, 100L, intervalTicks);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin != null) {
            Bukkit.getScheduler().runTaskLater(plugin,
                    this::updateAll, 1L);
        }
    }

    public void updateAll() {
        var players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) {
            return;
        }

        Map<UUID, PlayerTabData> data = new HashMap<>();
        int maxNameWidth = 0;
        int maxKarmaWidth = 0;

        for (Player player : players) {
            PlayerTabData td = collectData(player);
            data.put(player.getUniqueId(), td);
            if (td.nameWidth > maxNameWidth) {
                maxNameWidth = td.nameWidth;
            }
            if (td.karmaWidth > maxKarmaWidth) {
                maxKarmaWidth = td.karmaWidth;
            }
        }

        int targetNameCol = maxNameWidth + COL_PADDING;
        int targetKarmaCol = maxKarmaWidth + COL_PADDING;

        for (Player player : players) {
            PlayerTabData td = data.get(player.getUniqueId());
            if (td == null) {
                continue;
            }
            renderTab(player, td, targetNameCol, targetKarmaCol);
        }
    }

    public void updateTabName(Player player) {
        updateAll();
    }

    private PlayerTabData collectData(Player player) {
        String title = resolveKarmaTitle(player);
        String karma = resolveKarma(player);
        String titlePrefix = title.isEmpty() ? "" : title + " ";
        String plainName = titlePrefix + player.getName();
        int nameWidth = PixelWidth.textWidth(plainName);
        int karmaWidth = karma.isEmpty() ? 0
                : PixelWidth.textWidth(karma);
        return new PlayerTabData(
                title, karma, nameWidth, karmaWidth,
                player.getPing());
    }

    private void renderTab(Player player, PlayerTabData td,
                           int targetNameCol, int targetKarmaCol) {
        if (!enabledSupplier.get()) {
            player.playerListName(null);
            return;
        }

        String titlePrefix = td.title.isEmpty()
                ? "" : td.title + " ";

        Component result = miniMessage.deserialize(titlePrefix)
                .append(colorManager.renderGradient(
                        player.getName(), player))
                .append(PixelWidth.pad(td.nameWidth, targetNameCol));

        if (!td.karma.isEmpty()) {
            result = result.append(miniMessage.deserialize(
                    "<dark_gray>| " + karmaColor(td.karma) + td.karma))
                    .append(PixelWidth.pad(
                            td.karmaWidth, targetKarmaCol));
        }

        result = result.append(miniMessage.deserialize(
                "<dark_gray>| " + pingColor(td.ping)
                + td.ping + "ms"));

        player.playerListName(result);
    }

    private String resolveKarmaTitle(Player player) {
        if (!hasPapi) {
            return "";
        }
        String placeholder = titlePlaceholderSupplier.get();
        if (placeholder.isEmpty()) {
            return "";
        }
        String resolved = me.clip.placeholderapi.PlaceholderAPI
                .setPlaceholders(player, placeholder);
        if (resolved.isEmpty()) {
            return "";
        }
        return titleWrapSupplier.get().replace("<title>", resolved);
    }

    private String resolveKarma(Player player) {
        if (!hasPapi) {
            return "";
        }
        String placeholder = karmaPlaceholderSupplier.get();
        if (placeholder.isEmpty()) {
            return "";
        }
        return me.clip.placeholderapi.PlaceholderAPI
                .setPlaceholders(player, placeholder);
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

    private record PlayerTabData(String title, String karma,
                                 int nameWidth, int karmaWidth,
                                 int ping) {
    }
}
