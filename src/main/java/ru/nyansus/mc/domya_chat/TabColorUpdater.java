package ru.nyansus.mc.domya_chat;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TabColorUpdater implements Listener {

    private final PlayerColorManager colorManager;
    private final boolean enabled;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public TabColorUpdater(PlayerColorManager colorManager, boolean enabled) {
        this.colorManager = colorManager;
        this.enabled = enabled;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updateTabName(event.getPlayer());
    }

    public void updateTabName(Player player) {
        if (!enabled) {
            player.playerListName(null);
            return;
        }
        String[] colors = colorManager.getGradientColors(player);
        player.playerListName(miniMessage.deserialize(
                "<gradient:" + colors[0] + ":" + colors[1] + ">"
                + miniMessage.escapeTags(player.getName()) + "</gradient>"));
    }
}
