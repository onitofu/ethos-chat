package ru.nyansus.mc.ethos_chat;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Messages {

    private static final String DEFAULT_LOCALE = "en";
    private static final List<String> BUNDLED_LOCALES = List.of("en", "ru");

    private final JavaPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Map<String, YamlConfiguration> locales = new HashMap<>();

    public Messages(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void reload() {
        load();
    }

    private void load() {
        locales.clear();
        for (String locale : BUNDLED_LOCALES) {
            plugin.saveResource("messages_" + locale + ".yml", false);
        }
        File[] files = plugin.getDataFolder().listFiles(
                (dir, name) -> name.matches("messages_[a-z]+\\.yml"));
        if (files == null) {
            return;
        }
        for (File file : files) {
            String locale = file.getName().replace("messages_", "").replace(".yml", "");
            locales.put(locale, YamlConfiguration.loadConfiguration(file));
        }
    }

    public String get(String key, String locale) {
        YamlConfiguration config = locales.getOrDefault(locale, locales.get(DEFAULT_LOCALE));
        if (config == null) {
            return "?" + key;
        }
        String value = config.getString(key);
        if (value == null) {
            YamlConfiguration fallback = locales.get(DEFAULT_LOCALE);
            value = fallback != null ? fallback.getString(key) : null;
        }
        return value != null ? value : "?" + key;
    }

    public String getWorld(String worldName, String locale) {
        String key = "hover.worlds." + worldName;
        YamlConfiguration config = locales.getOrDefault(locale, locales.get(DEFAULT_LOCALE));
        if (config != null) {
            String value = config.getString(key);
            if (value != null) {
                return value;
            }
            YamlConfiguration fallback = locales.get(DEFAULT_LOCALE);
            if (fallback != null) {
                value = fallback.getString(key);
                if (value != null) {
                    return value;
                }
            }
        }
        return worldName;
    }

    public void send(CommandSender sender, String key, String... pairs) {
        String locale = sender instanceof Player player
                ? player.locale().getLanguage() : DEFAULT_LOCALE;
        String msg = get(key, locale);
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            msg = msg.replace(pairs[i], miniMessage.escapeTags(pairs[i + 1]));
        }
        sender.sendMessage(miniMessage.deserialize(msg));
    }
}
