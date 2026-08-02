package ru.nyansus.mc.ethos_chat.command;

import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.nyansus.mc.ethos_chat.Messages;
import ru.nyansus.mc.ethos_chat.rpname.NametagManager;
import ru.nyansus.mc.ethos_chat.rpname.RpNameManager;

public class NametagHeightCommand implements CommandExecutor, TabCompleter {

    private static final String PERMISSION = "ethos.chat.rpname";
    private static final double MIN_HEIGHT = -10.0;
    private static final double MAX_HEIGHT = 10.0;

    private final RpNameManager rpNameManager;
    private final Messages messages;
    private final NametagManager nametagManager;

    public NametagHeightCommand(RpNameManager rpNameManager, Messages messages,
                                NametagManager nametagManager) {
        this.rpNameManager = rpNameManager;
        this.messages = messages;
        this.nametagManager = nametagManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            messages.send(sender, "nametagheight.no-permission");
            return true;
        }
        if (args.length != 2) {
            messages.send(sender, "nametagheight.usage");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            messages.send(sender, "nametagheight.player-not-found", "{player}", args[0]);
            return true;
        }
        if (args[1].equalsIgnoreCase("reset")) {
            rpNameManager.resetNametagHeight(target.getUniqueId());
            nametagManager.refreshNametag(target);
            messages.send(sender, "nametagheight.reset", "{player}", target.getName());
            return true;
        }

        Double height = parseHeight(args[1]);
        if (height == null) {
            messages.send(sender, "nametagheight.invalid", "{height}", args[1]);
            return true;
        }
        rpNameManager.setNametagHeight(target.getUniqueId(), height);
        nametagManager.refreshNametag(target);
        messages.send(sender, "nametagheight.set",
                "{player}", target.getName(), "{height}", formatHeight(height));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                      String label, String[] args) {
        if (args.length == 1) {
            return TabCompleteHelper.completePlayerNames(args[0]);
        }
        if (args.length == 2 && "reset".startsWith(args[1].toLowerCase(Locale.ROOT))) {
            return List.of("reset");
        }
        return List.of();
    }

    private static Double parseHeight(String value) {
        try {
            double height = Double.parseDouble(value.replace(',', '.'));
            if (Double.isFinite(height) && height >= MIN_HEIGHT && height <= MAX_HEIGHT) {
                return height;
            }
        } catch (NumberFormatException ignored) {
            // The command reports the localized validation error below.
        }
        return null;
    }

    private static String formatHeight(double height) {
        return String.format(Locale.ROOT, "%+.2f", height);
    }
}
