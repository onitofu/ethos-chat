package ru.nyansus.mc.domya_chat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.nyansus.mc.domya_chat.DomyaChat;
import ru.nyansus.mc.domya_chat.Messages;

public class DomyaChatCommand implements CommandExecutor {

    private static final String PERMISSION = "domya.chat.admin";

    private final DomyaChat plugin;
    private final Messages messages;

    public DomyaChatCommand(DomyaChat plugin, Messages messages) {
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
