package ru.nyansus.mc.domya_chat.command;

import org.bukkit.entity.Player;
import ru.nyansus.mc.domya_chat.Messages;
import ru.nyansus.mc.domya_chat.rpname.NametagManager;
import ru.nyansus.mc.domya_chat.rpname.RpNameManager;

public class RpNameCommand extends BaseRpCommand {

    public RpNameCommand(RpNameManager rpNameManager, Messages messages, NametagManager nametagManager) {
        super(rpNameManager, messages, nametagManager, "rpname");
    }

    @Override
    protected void handleReset(Player target) {
        rpNameManager.resetRpName(target.getUniqueId());
    }

    @Override
    protected void handleSet(Player target, String value) {
        rpNameManager.setRpName(target.getUniqueId(), value);
    }

    @Override
    protected String valueKey() {
        return "{name}";
    }
}
