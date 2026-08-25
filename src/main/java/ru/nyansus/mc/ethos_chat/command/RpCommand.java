package ru.nyansus.mc.ethos_chat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.nyansus.mc.ethos_chat.Messages;
import ru.nyansus.mc.ethos_chat.rpname.RpNameManager;

public class RpCommand implements CommandExecutor {

    private final RpNameManager rpNameManager;
    private final Runnable tabRefresh;
    private final Messages messages;

    public RpCommand(RpNameManager rpNameManager, Runnable tabRefresh,
                     Messages messages) {
        this.rpNameManager = rpNameManager;
        this.tabRefresh = tabRefresh;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "command.player-only");
            return true;
        }
        boolean active = !rpNameManager.isRpActive(player.getUniqueId());
        rpNameManager.setRpActive(player.getUniqueId(), active);
        tabRefresh.run();
        messages.send(player, active ? "rp.active" : "rp.inactive");
        return true;
    }
}
