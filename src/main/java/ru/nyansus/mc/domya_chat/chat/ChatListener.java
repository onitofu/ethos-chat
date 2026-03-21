package ru.nyansus.mc.domya_chat.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.nyansus.mc.domya_chat.Messages;
import ru.nyansus.mc.domya_chat.color.PlayerColorManager;
import ru.nyansus.mc.domya_chat.rpname.RpNameManager;

public class ChatListener implements Listener {

    private final PlayerColorManager colorManager;
    private final RpNameManager rpNameManager;
    private final Messages messages;
    private final Supplier<String> formatSupplier;
    private final Supplier<LocalChatConfig> localChatConfigSupplier;
    private final Supplier<int[]> pingThresholdsSupplier;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ChatListener(PlayerColorManager colorManager, RpNameManager rpNameManager,
                        Messages messages, Supplier<String> formatSupplier,
                        Supplier<LocalChatConfig> localChatConfigSupplier,
                        Supplier<int[]> pingThresholdsSupplier) {
        this.colorManager = colorManager;
        this.rpNameManager = rpNameManager;
        this.messages = messages;
        this.formatSupplier = formatSupplier;
        this.localChatConfigSupplier = localChatConfigSupplier;
        this.pingThresholdsSupplier = pingThresholdsSupplier;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String format = formatSupplier.get();
        String rawMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        LocalChatConfig localChatConfig = localChatConfigSupplier.get();

        boolean isGlobal = false;
        String messageText = rawMessage;

        if (localChatConfig.enabled()) {
            if (rawMessage.startsWith(localChatConfig.globalPrefix())) {
                isGlobal = true;
                messageText = rawMessage.substring(localChatConfig.globalPrefix().length()).stripLeading();
            }
            if (!isGlobal) {
                Player source = event.getPlayer();
                double radiusSq = localChatConfig.radius() * localChatConfig.radius();
                Set<Player> nearby = source.getWorld().getPlayers().stream()
                        .filter(p -> p.getLocation().distanceSquared(source.getLocation()) <= radiusSq)
                        .collect(Collectors.toSet());
                event.viewers().removeIf(viewer -> viewer instanceof Player p
                        && !nearby.contains(p) && !p.equals(source));
            }
        }

        final boolean global = isGlobal;
        final String finalMessage = messageText;

        event.renderer((source, sourceDisplayName, message, viewer) -> {
            String locale = viewer instanceof Player viewerPlayer
                    ? viewerPlayer.locale().getLanguage() : "en";
            Component hover = buildHover(source, locale);
            Component playerComponent = colorManager
                    .renderGradient(rpNameManager.getDisplayName(source), source)
                    .hoverEvent(HoverEvent.showText(hover));
            TagResolver.Builder resolverBuilder = TagResolver.builder()
                    .resolver(Placeholder.component("player", playerComponent))
                    .resolver(Placeholder.unparsed("message", finalMessage));
            String finalFormat = format;
            if (localChatConfig.enabled()) {
                String prefixKey = global ? "chat.global-prefix" : "chat.local-prefix";
                String prefixLetter = messages.get(prefixKey, locale);
                String prefixFormat = global ? localChatConfig.globalFormat() : localChatConfig.localFormat();
                String chatPrefix = prefixFormat.replace("<prefix>", prefixLetter);
                finalFormat = chatPrefix + format;
            }
            return miniMessage.deserialize(finalFormat, resolverBuilder.build());
        });
    }

    private Component buildHover(Player source, String locale) {
        StringBuilder hover = new StringBuilder();
        if (rpNameManager.getRpName(source).isPresent()) {
            hover.append("<gray>")
                    .append(messages.get("hover.nickname", locale))
                    .append(" <dark_gray>» <white>")
                    .append(miniMessage.escapeTags(source.getName()))
                    .append("\n");
        }
        hover.append("<gray>")
                .append(messages.get("hover.ping", locale))
                .append("  <dark_gray>» ")
                .append(pingColor(source.getPing()))
                .append(source.getPing()).append("ms\n")
                .append("<gray>")
                .append(messages.get("hover.world", locale))
                .append(" <dark_gray>» <aqua>")
                .append(messages.getWorld(source.getWorld().getName(), locale));
        return miniMessage.deserialize(hover.toString());
    }

    private String pingColor(int ping) {
        int[] thresholds = pingThresholdsSupplier.get();
        if (ping < thresholds[0]) return "<green>";
        if (ping < thresholds[1]) return "<yellow>";
        return "<red>";
    }

    public record LocalChatConfig(boolean enabled, int radius, String globalPrefix,
                                  String localFormat, String globalFormat) {
    }
}
