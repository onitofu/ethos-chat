package ru.nyansus.mc.ethos_chat.chat;

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
import org.bukkit.scheduler.BukkitTask;
import ru.nyansus.mc.ethos_chat.color.PlayerColorManager;
import ru.nyansus.mc.ethos_chat.rpname.RpNameManager;

public class TabColorUpdater implements Listener {

    private static final String SEPARATOR = "<dark_gray>|";

    private final PlayerColorManager colorManager;
    private final RpNameManager rpNameManager;
    private final Supplier<TabConfig> configSupplier;
    private final Supplier<int[]> pingThresholdsSupplier;
    private final Supplier<String> titlePlaceholderSupplier;
    private final Supplier<String> karmaPlaceholderSupplier;
    private final Supplier<String> titleWrapSupplier;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final boolean hasPapi;
    private JavaPlugin plugin;
    private BukkitTask updateTask;

    public TabColorUpdater(PlayerColorManager colorManager,
                           RpNameManager rpNameManager,
                           Supplier<TabConfig> configSupplier,
                           Supplier<int[]> pingThresholdsSupplier,
                           Supplier<String> titlePlaceholderSupplier,
                           Supplier<String> karmaPlaceholderSupplier,
                           Supplier<String> titleWrapSupplier) {
        this.colorManager = colorManager;
        this.rpNameManager = rpNameManager;
        this.configSupplier = configSupplier;
        this.pingThresholdsSupplier = pingThresholdsSupplier;
        this.titlePlaceholderSupplier = titlePlaceholderSupplier;
        this.karmaPlaceholderSupplier = karmaPlaceholderSupplier;
        this.titleWrapSupplier = titleWrapSupplier;
        this.hasPapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public void startUpdateTask(JavaPlugin pluginInstance, long intervalTicks) {
        this.plugin = pluginInstance;
        if (updateTask != null) {
            updateTask.cancel();
        }
        updateTask = Bukkit.getScheduler().runTaskTimer(
                pluginInstance, this::updateAll, 100L, Math.max(1L, intervalTicks));
    }

    public void stop() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playerListName(null);
            resetListOrder(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin != null) {
            Bukkit.getScheduler().runTaskLater(plugin, this::updateAll, 1L);
        }
    }

    public void updateAll() {
        var players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) {
            return;
        }
        TabConfig config = configSupplier.get();
        if (!config.enabled) {
            for (Player player : players) {
                player.playerListName(null);
                resetListOrder(player);
            }
            return;
        }

        Map<UUID, PlayerTabData> data = new HashMap<>();
        int maxNameWidth = 0;
        int maxKarmaWidth = 0;
        int maxPingWidth = 0;
        boolean hasKarma = false;

        for (Player player : players) {
            PlayerTabData tabData = collectData(player, config);
            data.put(player.getUniqueId(), tabData);
            maxNameWidth = Math.max(maxNameWidth, tabData.nameWidth);
            maxKarmaWidth = Math.max(maxKarmaWidth, tabData.karmaWidth);
            maxPingWidth = Math.max(maxPingWidth, tabData.pingWidth);
            hasKarma |= !tabData.karma.isEmpty();
        }

        boolean showKarma = config.showKarma && hasKarma;
        for (Player player : players) {
            PlayerTabData tabData = data.get(player.getUniqueId());
            if (tabData != null) {
                updateListOrder(player, tabData.rpActive, config.sortRpFirst);
                renderTab(player, tabData, config, showKarma,
                        maxNameWidth, maxKarmaWidth, maxPingWidth);
            }
        }
    }

    public void updateTabName(Player player) {
        updateAll();
    }

    private PlayerTabData collectData(Player player, TabConfig config) {
        String title = config.showTitle ? resolveKarmaTitle(player) : "";
        String karma = config.showKarma ? resolveKarma(player) : "";
        String titlePrefix = title.isEmpty() ? "" : title + " ";
        String plainName = titlePrefix + player.getName();
        String ping = player.getPing() + "ms";
        return new PlayerTabData(
                title,
                karma,
                PixelWidth.textWidth(plainName),
                karma.isEmpty() ? 0 : PixelWidth.textWidth(karma),
                ping,
                PixelWidth.textWidth(ping),
                rpNameManager.isRpActive(player.getUniqueId()));
    }

    private void renderTab(Player player, PlayerTabData data, TabConfig config,
                           boolean showKarma, int maxNameWidth,
                           int maxKarmaWidth, int maxPingWidth) {
        Component result = Component.empty();
        if (config.showRpStatus) {
            String rpStatus = data.rpActive ? "<green>●" : "<red>●";
            result = result.append(miniMessage.deserialize(
                    " " + rpStatus + " " + SEPARATOR + " "));
        }

        String titlePrefix = data.title.isEmpty() ? "" : data.title + " ";
        result = result.append(miniMessage.deserialize(titlePrefix))
                .append(colorManager.renderGradient(player.getName(), player));

        if (showKarma) {
            result = appendNameSeparator(result, data.nameWidth,
                    maxNameWidth, config.columnGap)
                    .append(PixelWidth.pad(config.columnGap
                            + maxKarmaWidth - data.karmaWidth));
            if (!data.karma.isEmpty()) {
                result = result.append(miniMessage.deserialize(
                        karmaColor(data.karma) + data.karma));
            }
            if (config.showPing) {
                result = appendValueSeparator(result, config.columnGap)
                        .append(PixelWidth.pad(config.columnGap
                                + maxPingWidth - data.pingWidth))
                        .append(miniMessage.deserialize(
                                pingColor(player.getPing()) + data.ping));
            }
        } else if (config.showPing) {
            result = appendNameSeparator(result, data.nameWidth,
                    maxNameWidth, config.columnGap)
                    .append(PixelWidth.pad(config.columnGap
                            + maxPingWidth - data.pingWidth))
                    .append(miniMessage.deserialize(
                            pingColor(player.getPing()) + data.ping));
        }

        player.playerListName(result);
    }

    private Component appendNameSeparator(Component component, int nameWidth,
                                          int maxNameWidth, int columnGap) {
        return component.append(PixelWidth.pad(columnGap + maxNameWidth - nameWidth))
                .append(miniMessage.deserialize(SEPARATOR));
    }

    private Component appendValueSeparator(Component component, int columnGap) {
        return component.append(PixelWidth.pad(columnGap))
                .append(miniMessage.deserialize(SEPARATOR));
    }

    private void updateListOrder(Player player, boolean rpActive,
                                 boolean sortRpFirst) {
        int order = sortRpFirst && rpActive ? 1 : 0;
        if (player.getPlayerListOrder() != order) {
            player.setPlayerListOrder(order);
        }
    }

    private void resetListOrder(Player player) {
        if (player.getPlayerListOrder() != 0) {
            player.setPlayerListOrder(0);
        }
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
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, placeholder);
    }

    private String karmaColor(String karma) {
        try {
            int value = Integer.parseInt(karma);
            if (value > 0) return "<green>";
            if (value < 0) return "<red>";
        } catch (NumberFormatException ignored) {
            // Use gray for non-numeric karma values.
        }
        return "<gray>";
    }

    private String pingColor(int ping) {
        int[] thresholds = pingThresholdsSupplier.get();
        if (ping < thresholds[0]) return "<green>";
        if (ping < thresholds[1]) return "<yellow>";
        return "<red>";
    }

    public record TabConfig(boolean enabled, boolean showTitle,
                            boolean showKarma, boolean showPing,
                            boolean showRpStatus, boolean sortRpFirst,
                            int columnGap) {
    }

    private record PlayerTabData(String title, String karma,
                                 int nameWidth, int karmaWidth,
                                 String ping, int pingWidth,
                                 boolean rpActive) {
    }
}
