package ru.nyansus.mc.ethos_chat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.nyansus.mc.ethos_chat.Messages;
import ru.nyansus.mc.ethos_chat.afk.AfkManager;

public class AfkCommand implements CommandExecutor {

    private final AfkManager afkManager;
    private final Messages messages;

    public AfkCommand(AfkManager afkManager, Messages messages) {
        this.afkManager = afkManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "command.player-only");
            return true;
        }
        afkManager.toggle(player);
        return true;
    }
}
