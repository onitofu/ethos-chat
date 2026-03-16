package ru.nyansus.mc.domya_chat;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlPlayerColorStorage implements PlayerColorStorage {

    private final File file;
    private final Logger logger;
    private YamlConfiguration config;

    public YamlPlayerColorStorage(File file, Logger logger) {
        this.file = file;
        this.logger = logger;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public synchronized Optional<String> getColor(UUID uuid) {
        return Optional.ofNullable(config.getString(uuid.toString()));
    }

    @Override
    public synchronized void setColor(UUID uuid, String hex) {
        config.set(uuid.toString(), hex);
        save();
    }

    @Override
    public synchronized void removeColor(UUID uuid) {
        config.set(uuid.toString(), null);
        save();
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            logger.warning("Failed to save players.yml: " + e.getMessage());
        }
    }
}
