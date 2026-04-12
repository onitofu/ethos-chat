package ru.nyansus.mc.ethos_chat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.nyansus.mc.ethos_chat.EthosChat;
import ru.nyansus.mc.ethos_chat.Messages;

public class EthosChatCommand implements CommandExecutor {

    private static final String PERMISSION = "ethos.chat.admin";

    private final EthosChat plugin;
    private final Messages messages;

    public EthosChatCommand(EthosChat plugin, Messages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            messages.send(sender, "admin.no-permission");
            return true;
        }
        if (args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
            messages.send(sender, "admin.usage");
            return true;
        }
        plugin.performReload();
        messages.send(sender, "admin.reloaded");
        return true;
    }
}
