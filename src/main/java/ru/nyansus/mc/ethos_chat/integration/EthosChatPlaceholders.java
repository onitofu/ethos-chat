package ru.nyansus.mc.ethos_chat.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nyansus.mc.ethos_chat.rpname.RpNameManager;

public final class EthosChatPlaceholders extends PlaceholderExpansion {

    private final String version;
    private final RpNameManager rpNameManager;

    public EthosChatPlaceholders(String version, RpNameManager rpNameManager) {
        this.version = version;
        this.rpNameManager = rpNameManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ethoschat";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Domya SMP";
    }

    @Override
    public @NotNull String getVersion() {
        return version;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) {
            return "";
        }

        return resolve(offlinePlayer, params);
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        return resolve(player, params);
    }

    private String resolve(OfflinePlayer offlinePlayer, String params) {
        if (params.equalsIgnoreCase("rpname")) {
            return rpNameManager.getRpName(offlinePlayer.getUniqueId()).orElse("");
        }
        if (params.equalsIgnoreCase("display_name")) {
            return rpNameManager.getDisplayName(offlinePlayer);
        }
        if (params.equalsIgnoreCase("race")) {
            return rpNameManager.getRace(offlinePlayer.getUniqueId()).orElse("");
        }
        if (params.equalsIgnoreCase("has_rpname")) {
            return rpNameManager.getRpName(offlinePlayer.getUniqueId()).isPresent() ? "true" : "false";
        }
        if (params.equalsIgnoreCase("has_race")) {
            return rpNameManager.getRace(offlinePlayer.getUniqueId()).isPresent() ? "true" : "false";
        }
        return null;
    }
}
