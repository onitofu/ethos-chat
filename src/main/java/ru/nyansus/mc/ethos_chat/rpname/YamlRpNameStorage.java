package ru.nyansus.mc.ethos_chat.rpname;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlRpNameStorage implements RpNameStorage {

    private final File file;
    private final Logger logger;
    private final YamlConfiguration config;

    public YamlRpNameStorage(File file, Logger logger) {
        this.file = file;
        this.logger = logger;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public synchronized Optional<String> getRpName(UUID uuid) {
        return Optional.ofNullable(config.getString(uuid + ".name"));
    }

    @Override
    public synchronized void setRpName(UUID uuid, String name) {
        config.set(uuid + ".name", name);
        save();
    }

    @Override
    public synchronized void removeRpName(UUID uuid) {
        config.set(uuid + ".name", null);
        cleanupEntry(uuid);
        save();
    }

    @Override
    public synchronized Optional<String> getRace(UUID uuid) {
        return Optional.ofNullable(config.getString(uuid + ".race"));
    }

    @Override
    public synchronized void setRace(UUID uuid, String race) {
        config.set(uuid + ".race", race);
        save();
    }

    @Override
    public synchronized void removeRace(UUID uuid) {
        config.set(uuid + ".race", null);
        cleanupEntry(uuid);
        save();
    }

    @Override
    public synchronized OptionalDouble getNametagHeight(UUID uuid) {
        String path = uuid + ".nametag-height";
        return config.contains(path)
                ? OptionalDouble.of(config.getDouble(path)) : OptionalDouble.empty();
    }

    @Override
    public synchronized void setNametagHeight(UUID uuid, double height) {
        config.set(uuid + ".nametag-height", height);
        save();
    }

    @Override
    public synchronized void removeNametagHeight(UUID uuid) {
        config.set(uuid + ".nametag-height", null);
        cleanupEntry(uuid);
        save();
    }

    private void cleanupEntry(UUID uuid) {
        var section = config.getConfigurationSection(uuid.toString());
        if (section != null && section.getKeys(false).isEmpty()) {
            config.set(uuid.toString(), null);
        }
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            logger.warning("Failed to save rpnames.yml: " + e.getMessage());
        }
    }
}
