package ru.nyansus.mc.domya_chat;

import java.io.File;
import java.util.List;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public class DomyaChat extends JavaPlugin {

    private static final String DEFAULT_FORMAT = "<dark_gray>▶ <player> <dark_gray>» <gray><message>";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Messages messages = new Messages(this);
        PlayerColorStorage storage = new YamlPlayerColorStorage(
                new File(getDataFolder(), "players.yml"), getLogger());
        float gradientShift = (float) getConfig().getDouble("gradient-shift", 25);
        List<String> defaultColors = getConfig().getStringList("default-colors");
        if (defaultColors.isEmpty()) {
            defaultColors = List.of("dark_green", "dark_aqua", "dark_red", "dark_purple",
                    "gold", "blue", "green", "aqua", "red", "light_purple", "yellow");
        }
        PlayerColorManager colorManager = new PlayerColorManager(storage, gradientShift, defaultColors);
        ChatListener listener = new ChatListener(colorManager, messages,
                () -> getConfig().getString("format", DEFAULT_FORMAT));
        getServer().getPluginManager().registerEvents(listener, this);
        String access = getConfig().getString("command-access", "op");
        PermissionDefault permDefault = "all".equalsIgnoreCase(access)
                ? PermissionDefault.TRUE : PermissionDefault.OP;
        Permission perm = getServer().getPluginManager().getPermission("domya.chat.color");
        if (perm != null) {
            perm.setDefault(permDefault);
        }
        boolean tabColors = getConfig().getBoolean("tab-colors", true);
        TabColorUpdater tabUpdater = new TabColorUpdater(colorManager, tabColors);
        getServer().getPluginManager().registerEvents(tabUpdater, this);
        ChatColorCommand command = new ChatColorCommand(colorManager, messages, tabUpdater);
        getCommand("chatcolor").setExecutor(command);
        getCommand("chatcolor").setTabCompleter(command);
    }
}
