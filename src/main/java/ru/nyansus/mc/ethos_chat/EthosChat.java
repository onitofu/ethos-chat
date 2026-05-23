package ru.nyansus.mc.ethos_chat;

import java.io.File;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import ru.nyansus.mc.ethos_chat.chat.ChatListener;
import ru.nyansus.mc.ethos_chat.chat.TabColorUpdater;
import ru.nyansus.mc.ethos_chat.color.PlayerColorManager;
import ru.nyansus.mc.ethos_chat.color.PlayerColorStorage;
import ru.nyansus.mc.ethos_chat.color.YamlPlayerColorStorage;
import ru.nyansus.mc.ethos_chat.command.ChatColorCommand;
import ru.nyansus.mc.ethos_chat.command.EthosChatCommand;
import ru.nyansus.mc.ethos_chat.command.RealNameCommand;
import ru.nyansus.mc.ethos_chat.command.RpNameCommand;
import ru.nyansus.mc.ethos_chat.command.RpRaceCommand;
import ru.nyansus.mc.ethos_chat.integration.EthosChatPlaceholders;
import ru.nyansus.mc.ethos_chat.rpname.NametagManager;
import ru.nyansus.mc.ethos_chat.rpname.RpNameManager;
import ru.nyansus.mc.ethos_chat.rpname.RpNameStorage;
import ru.nyansus.mc.ethos_chat.rpname.YamlRpNameStorage;

public class EthosChat extends JavaPlugin {

    private static final String DEFAULT_FORMAT =
            "<dark_gray>▶ <title><player> <dark_gray>» <gray><message>";

    private Messages messages;
    private NametagManager nametagManager;
    private TabColorUpdater tabUpdater;
    private EthosChatPlaceholders placeholders;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        messages = new Messages(this);
        PlayerColorStorage storage = new YamlPlayerColorStorage(
                new File(getDataFolder(), "players.yml"), getLogger());
        List<String> defaultColors = getConfig().getStringList("default-colors");
        if (defaultColors.isEmpty()) {
            defaultColors = List.of("dark_green", "dark_aqua", "dark_red",
                    "dark_purple", "gold", "blue", "green", "aqua",
                    "red", "light_purple", "yellow");
        }
        PlayerColorManager colorManager = new PlayerColorManager(storage,
                (float) getConfig().getDouble("gradient-shift", 25),
                defaultColors);
        RpNameStorage rpNameStorage = new YamlRpNameStorage(
                new File(getDataFolder(), "rpnames.yml"), getLogger());
        RpNameManager rpNameManager = new RpNameManager(rpNameStorage);
        registerPlaceholders(rpNameManager);
        java.util.function.Supplier<int[]> pingThresholds = () -> new int[]{
                getConfig().getInt("ping-thresholds.good", 50),
                getConfig().getInt("ping-thresholds.bad", 150)};
        java.util.function.Supplier<String> titlePlaceholder =
                () -> getConfig().getString("placeholders.title", "");
        java.util.function.Supplier<String> karmaPlaceholder =
                () -> getConfig().getString("placeholders.karma", "");
        java.util.function.Supplier<String> titleWrap =
                () -> getConfig().getString("placeholders.title-wrap", "<title>");
        ChatListener listener = new ChatListener(colorManager, rpNameManager,
                messages,
                () -> getConfig().getString("format", DEFAULT_FORMAT),
                this::loadLocalChatConfig, pingThresholds,
                titlePlaceholder, titleWrap);
        getServer().getPluginManager().registerEvents(listener, this);
        applyPermissions();
        tabUpdater = new TabColorUpdater(colorManager,
                () -> getConfig().getBoolean("tab-colors", true),
                pingThresholds, titlePlaceholder, karmaPlaceholder, titleWrap);
        tabUpdater.startUpdateTask(this,
                getConfig().getLong("tab-update-interval", 200L));
        getServer().getPluginManager().registerEvents(tabUpdater, this);
        nametagManager = new NametagManager(
                rpNameManager, colorManager, this, this::loadNametagConfig);
        getServer().getPluginManager().registerEvents(nametagManager, this);
        ChatColorCommand colorCommand = new ChatColorCommand(
                colorManager, messages, tabUpdater, nametagManager);
        getCommand("chatcolor").setExecutor(colorCommand);
        getCommand("chatcolor").setTabCompleter(colorCommand);
        RpNameCommand rpNameCommand =
                new RpNameCommand(rpNameManager, messages, nametagManager);
        getCommand("rpname").setExecutor(rpNameCommand);
        getCommand("rpname").setTabCompleter(rpNameCommand);
        RpRaceCommand rpRaceCommand =
                new RpRaceCommand(rpNameManager, messages, nametagManager);
        getCommand("rprace").setExecutor(rpRaceCommand);
        getCommand("rprace").setTabCompleter(rpRaceCommand);
        RealNameCommand realNameCommand =
                new RealNameCommand(rpNameManager, messages);
        getCommand("realname").setExecutor(realNameCommand);
        getCommand("realname").setTabCompleter(realNameCommand);
        EthosChatCommand ethosChatCommand =
                new EthosChatCommand(this, messages);
        getCommand("ethoschat").setExecutor(ethosChatCommand);
    }

    @Override
    public void onDisable() {
        if (placeholders != null) {
            placeholders.unregister();
            placeholders = null;
        }
        if (nametagManager != null) {
            nametagManager.removeAll();
        }
    }

    public void performReload() {
        reloadConfig();
        messages.reload();
        applyPermissions();
        nametagManager.refreshAll();
        for (Player player : Bukkit.getOnlinePlayers()) {
            tabUpdater.updateTabName(player);
        }
    }

    private void applyPermissions() {
        applyPermission("chatcolor-access", "ethos.chat.color");
        applyPermission("rpname-access", "ethos.chat.rpname");
    }

    private void applyPermission(String configKey, String permissionName) {
        String access = getConfig().getString(configKey, "op");
        PermissionDefault permDefault = "all".equalsIgnoreCase(access)
                ? PermissionDefault.TRUE : PermissionDefault.OP;
        Permission perm = getServer().getPluginManager().getPermission(permissionName);
        if (perm != null) {
            perm.setDefault(permDefault);
        }
    }

    private void registerPlaceholders(RpNameManager rpNameManager) {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return;
        }
        placeholders = new EthosChatPlaceholders(getDescription().getVersion(), rpNameManager);
        if (placeholders.register()) {
            getLogger().info("Registered PlaceholderAPI expansion: ethoschat");
        } else {
            getLogger().warning("Failed to register PlaceholderAPI expansion: ethoschat");
        }
    }

    private ChatListener.LocalChatConfig loadLocalChatConfig() {
        String defLocal = "<dark_gray>[<gray><prefix><dark_gray>] ";
        String defGlobal = "<dark_gray>[<yellow><prefix><dark_gray>] ";
        return new ChatListener.LocalChatConfig(
                getConfig().getBoolean("local-chat.enabled", false),
                getConfig().getInt("local-chat.radius", 128),
                getConfig().getString("local-chat.global-prefix", "!"),
                getConfig().getString("local-chat.local-format", defLocal),
                getConfig().getString("local-chat.global-format", defGlobal));
    }

    private NametagManager.NametagConfig loadNametagConfig() {
        return new NametagManager.NametagConfig(
                (float) getConfig().getDouble("nametag.name-offset", 0.4),
                (float) getConfig().getDouble("nametag.race-offset", 0.2),
                (float) getConfig().getDouble("nametag.race-scale", 0.6),
                getConfig().getString("nametag.race-format", "\u00AB{race}\u00BB"));
    }
}
