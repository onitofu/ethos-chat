package ru.nyansus.mc.ethos_chat.command;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import ru.nyansus.mc.ethos_chat.Messages;
import ru.nyansus.mc.ethos_chat.rpname.RpNameManager;

public class RpRaceCommand extends BaseRpCommand {

    public RpRaceCommand(RpNameManager rpNameManager, Messages messages,
                         Consumer<Player> nametagRefresh) {
        super(rpNameManager, messages, nametagRefresh, "rprace");
    }

    @Override
    protected void handleReset(Player target) {
        rpNameManager.resetRace(target.getUniqueId());
    }

    @Override
    protected void handleSet(Player target, String value) {
        rpNameManager.setRace(target.getUniqueId(), value);
    }

    @Override
    protected String valueKey() {
        return "{race}";
    }
}
