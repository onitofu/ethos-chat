package ru.nyansus.mc.domya_chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final PlayerColorManager colorManager;
    private final Messages messages;
    private final Supplier<String> formatSupplier;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ChatListener(PlayerColorManager colorManager, Messages messages, Supplier<String> formatSupplier) {
        this.colorManager = colorManager;
        this.messages = messages;
        this.formatSupplier = formatSupplier;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String format = formatSupplier.get();
        String[] colors = colorManager.getGradientColors(event.getPlayer());
        event.renderer((source, sourceDisplayName, message, viewer) -> {
            String locale = viewer instanceof Player viewerPlayer
                    ? viewerPlayer.locale().getLanguage() : "en";
            Component hover = buildHover(source, locale);
            Component playerComponent = miniMessage.deserialize(
                    "<gradient:" + colors[0] + ":" + colors[1] + ">"
                    + miniMessage.escapeTags(source.getName()) + "</gradient>")
                    .hoverEvent(HoverEvent.showText(hover));
            String messageStr = PlainTextComponentSerializer.plainText().serialize(message);
            TagResolver resolver = TagResolver.builder()
                    .resolver(Placeholder.component("player", playerComponent))
                    .resolver(Placeholder.unparsed("message", messageStr))
                    .build();
            return miniMessage.deserialize(format, resolver);
        });
    }

    private Component buildHover(Player source, String locale) {
        return miniMessage.deserialize(
                "<gray>" + messages.get("hover.ping", locale) + "  <dark_gray>» "
                + pingColor(source.getPing()) + source.getPing() + "ms\n"
                + "<gray>" + messages.get("hover.world", locale) + " <dark_gray>» <aqua>"
                + messages.getWorld(source.getWorld().getName(), locale));
    }

    private static String pingColor(int ping) {
        if (ping < 50) return "<green>";
        if (ping < 150) return "<yellow>";
        return "<red>";
    }
}
