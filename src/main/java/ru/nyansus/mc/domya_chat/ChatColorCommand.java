package ru.nyansus.mc.domya_chat;

import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ChatColorCommand implements CommandExecutor, TabCompleter {

    private static final String PERMISSION = "domya.chat.color";

    private final PlayerColorManager colorManager;
    private final Messages messages;
    private final TabColorUpdater tabUpdater;

    public ChatColorCommand(PlayerColorManager colorManager, Messages messages, TabColorUpdater tabUpdater) {
        this.colorManager = colorManager;
        this.messages = messages;
        this.tabUpdater = tabUpdater;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            messages.send(sender, "command.no-permission");
            return true;
        }
        if (args.length < 2) {
            messages.send(sender, "command.usage");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            messages.send(sender, "command.player-not-found", "{player}", args[0]);
            return true;
        }
        if (args[1].equalsIgnoreCase("reset")) {
            colorManager.resetColor(target.getUniqueId());
            tabUpdater.updateTabName(target);
            messages.send(sender, "command.color-reset", "{player}", target.getName());
            return true;
        }
        String hex = ColorConverter.resolveHex(args[1]);
        if (hex == null) {
            messages.send(sender, "command.invalid-color", "{color}", args[1]);
            return true;
        }
        colorManager.setColor(target.getUniqueId(), hex);
        tabUpdater.updateTabName(target);
        messages.send(sender, "command.color-set", "{player}", target.getName(), "{color}", args[1].toLowerCase());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            List<String> options = PlayerColorManager.NAMED_COLORS.stream()
                    .map(c -> NamedTextColor.NAMES.key(c))
                    .collect(Collectors.toList());
            options.add("reset");
            return options.stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
