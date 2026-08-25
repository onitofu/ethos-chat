package ru.nyansus.mc.ethos_chat.command;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import ru.nyansus.mc.ethos_chat.Messages;
import ru.nyansus.mc.ethos_chat.rpname.RpNameManager;

public class RpNameCommand extends BaseRpCommand {

    public RpNameCommand(RpNameManager rpNameManager, Messages messages,
                         Consumer<Player> nametagRefresh) {
        super(rpNameManager, messages, nametagRefresh, "rpname");
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
