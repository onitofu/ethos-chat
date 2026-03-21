package ru.nyansus.mc.domya_chat.command;

import org.bukkit.entity.Player;
import ru.nyansus.mc.domya_chat.Messages;
import ru.nyansus.mc.domya_chat.rpname.NametagManager;
import ru.nyansus.mc.domya_chat.rpname.RpNameManager;

public class RpRaceCommand extends BaseRpCommand {

    public RpRaceCommand(RpNameManager rpNameManager, Messages messages, NametagManager nametagManager) {
        super(rpNameManager, messages, nametagManager, "rprace");
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
