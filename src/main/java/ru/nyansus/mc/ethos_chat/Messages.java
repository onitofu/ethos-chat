package ru.nyansus.mc.ethos_chat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private String defaultLocale = DEFAULT_LOCALE;

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
            File localeFile = new File(plugin.getDataFolder(), "lang/" + locale + ".yml");
            if (!localeFile.exists()) {
                plugin.saveResource("lang/" + locale + ".yml", false);
            }
        }
        File languageDirectory = new File(plugin.getDataFolder(), "lang");
        File[] files = languageDirectory.listFiles(
                (dir, name) -> name.matches("[a-z]+\\.yml"));
        if (files == null) {
            return;
        }
        for (File file : files) {
            String locale = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            applyBundledDefaults("lang/" + file.getName(), config);
            locales.put(locale, config);
        }
        defaultLocale = plugin.getConfig().getString("default-locale", DEFAULT_LOCALE)
                .toLowerCase(Locale.ROOT);
        if (!locales.containsKey(defaultLocale)) {
            defaultLocale = DEFAULT_LOCALE;
        }
    }

    private void applyBundledDefaults(String resourceName, YamlConfiguration config) {
        try (InputStream stream = plugin.getResource(resourceName)) {
            if (stream == null) {
                return;
            }
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            config.setDefaults(YamlConfiguration.loadConfiguration(reader));
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load defaults for "
                    + resourceName + ": " + e.getMessage());
        }
    }

    public String get(String key, String locale) {
        YamlConfiguration config = locales.getOrDefault(locale, locales.get(defaultLocale));
        if (config == null) {
            return "?" + key;
        }
        String value = config.getString(key);
        if (value == null) {
            YamlConfiguration fallback = locales.get(defaultLocale);
            value = fallback != null ? fallback.getString(key) : null;
        }
        return value != null ? value : "?" + key;
    }

    public String getWorld(String worldName, String locale) {
        String key = "hover.worlds." + worldName;
        YamlConfiguration config = locales.getOrDefault(locale, locales.get(defaultLocale));
        if (config != null) {
            String value = config.getString(key);
            if (value != null) {
                return value;
            }
            YamlConfiguration fallback = locales.get(defaultLocale);
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
                ? player.locale().getLanguage() : defaultLocale;
        String msg = get(key, locale);
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            msg = msg.replace(pairs[i], miniMessage.escapeTags(pairs[i + 1]));
        }
        sender.sendMessage(miniMessage.deserialize(msg));
    }
}
