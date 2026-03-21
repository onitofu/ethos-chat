package ru.nyansus.mc.domya_chat.command;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.nyansus.mc.domya_chat.Messages;
import ru.nyansus.mc.domya_chat.rpname.NametagManager;
import ru.nyansus.mc.domya_chat.rpname.RpNameManager;

public abstract class BaseRpCommand implements CommandExecutor, TabCompleter {

    private static final String PERMISSION = "domya.chat.rpname";

    protected final RpNameManager rpNameManager;
    protected final Messages messages;
    protected final NametagManager nametagManager;
    private final String messagePrefix;

    protected BaseRpCommand(RpNameManager rpNameManager, Messages messages,
                            NametagManager nametagManager, String messagePrefix) {
        this.rpNameManager = rpNameManager;
        this.messages = messages;
        this.nametagManager = nametagManager;
        this.messagePrefix = messagePrefix;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            messages.send(sender, messagePrefix + ".no-permission");
            return true;
        }
        if (args.length < 2) {
            messages.send(sender, messagePrefix + ".usage");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            messages.send(sender, messagePrefix + ".player-not-found", "{player}", args[0]);
            return true;
        }
        if (args[1].equalsIgnoreCase("reset")) {
            handleReset(target);
            nametagManager.refreshNametag(target);
            messages.send(sender, messagePrefix + ".reset", "{player}", target.getName());
            return true;
        }
        String value = joinArgs(args);
        handleSet(target, value);
        nametagManager.refreshNametag(target);
        messages.send(sender, messagePrefix + ".set",
                "{player}", target.getName(), valueKey(), value);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return TabCompleteHelper.completePlayerNames(args[0]);
        }
        if (args.length == 2) {
            return List.of("reset").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    protected abstract void handleReset(Player target);

    protected abstract void handleSet(Player target, String value);

    protected abstract String valueKey();

    private static String joinArgs(String[] args) {
        StringBuilder sb = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++) {
            sb.append(' ').append(args[i]);
        }
        return sb.toString();
    }
}
