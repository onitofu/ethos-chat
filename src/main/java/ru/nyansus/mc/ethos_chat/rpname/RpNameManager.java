package ru.nyansus.mc.ethos_chat.rpname;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class RpNameManager {

    private final RpNameStorage storage;

    public RpNameManager(RpNameStorage storage) {
        this.storage = storage;
    }

    public Optional<String> getRpName(Player player) {
        return storage.getRpName(player.getUniqueId());
    }

    public Optional<String> getRpName(UUID uuid) {
        return storage.getRpName(uuid);
    }

    public String getDisplayName(Player player) {
        return storage.getRpName(player.getUniqueId()).orElse(player.getName());
    }

    public String getDisplayName(OfflinePlayer player) {
        return storage.getRpName(player.getUniqueId()).orElseGet(() -> {
            String name = player.getName();
            return name == null ? "" : name;
        });
    }

    public void setRpName(UUID uuid, String name) {
        storage.setRpName(uuid, name);
    }

    public void resetRpName(UUID uuid) {
        storage.removeRpName(uuid);
    }

    public Optional<String> getRace(Player player) {
        return storage.getRace(player.getUniqueId());
    }

    public Optional<String> getRace(UUID uuid) {
        return storage.getRace(uuid);
    }

    public void setRace(UUID uuid, String race) {
        storage.setRace(uuid, race);
    }

    public void resetRace(UUID uuid) {
        storage.removeRace(uuid);
    }

    public boolean hasNametag(Player player) {
        return storage.getRpName(player.getUniqueId()).isPresent()
                || storage.getRace(player.getUniqueId()).isPresent();
    }
}
