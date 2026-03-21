package ru.nyansus.mc.domya_chat.chat;

import java.util.function.Supplier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.nyansus.mc.domya_chat.color.PlayerColorManager;

public class TabColorUpdater implements Listener {

    private final PlayerColorManager colorManager;
    private final Supplier<Boolean> enabledSupplier;

    public TabColorUpdater(PlayerColorManager colorManager, Supplier<Boolean> enabledSupplier) {
        this.colorManager = colorManager;
        this.enabledSupplier = enabledSupplier;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updateTabName(event.getPlayer());
    }

    public void updateTabName(Player player) {
        if (!enabledSupplier.get()) {
            player.playerListName(null);
            return;
        }
        player.playerListName(colorManager.renderGradient(player.getName(), player));
    }
}
