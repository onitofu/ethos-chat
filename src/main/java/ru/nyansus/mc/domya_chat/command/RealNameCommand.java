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
import ru.nyansus.mc.domya_chat.rpname.RpNameManager;

public class RealNameCommand implements CommandExecutor, TabCompleter {

    private final RpNameManager rpNameManager;
    private final Messages messages;

    public RealNameCommand(RpNameManager rpNameManager, Messages messages) {
        this.rpNameManager = rpNameManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            messages.send(sender, "realname.usage");
            return true;
        }
        String query = String.join(" ", args).toLowerCase();
        for (Player player : Bukkit.getOnlinePlayers()) {
            String rpName = rpNameManager.getDisplayName(player);
            if (rpName.toLowerCase().equals(query)) {
                messages.send(sender, "realname.found",
                        "{name}", rpName, "{player}", player.getName());
                return true;
            }
        }
        messages.send(sender, "realname.not-found",
                "{name}", String.join(" ", args));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(p -> rpNameManager.getRpName(p).isPresent())
                    .map(rpNameManager::getDisplayName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
