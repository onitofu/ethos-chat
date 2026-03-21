package ru.nyansus.mc.domya_chat.color;

import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class PlayerColorManager {

    public static final List<NamedTextColor> NAMED_COLORS = List.of(
            NamedTextColor.DARK_GREEN,
            NamedTextColor.DARK_AQUA,
            NamedTextColor.DARK_RED,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.GOLD,
            NamedTextColor.BLUE,
            NamedTextColor.GREEN,
            NamedTextColor.AQUA,
            NamedTextColor.RED,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.YELLOW
    );

    private final PlayerColorStorage storage;
    private final float gradientShift;
    private final List<String> defaultColors;

    public PlayerColorManager(PlayerColorStorage storage, float gradientShift, List<String> defaultColors) {
        this.storage = storage;
        this.gradientShift = gradientShift;
        this.defaultColors = defaultColors;
    }

    public String[] getGradientColors(Player player) {
        String hex1 = storage.getColor(player.getUniqueId()).orElseGet(() -> {
            String generated = generateColor(player.getUniqueId());
            storage.setColor(player.getUniqueId(), generated);
            return generated;
        });
        if (gradientShift == 0) {
            return new String[]{hex1, hex1};
        }
        float[] hsl = ColorConverter.hexToHsl(hex1);
        float hue2 = (hsl[0] + gradientShift) % 360;
        return new String[]{hex1, ColorConverter.hslToHex(hue2, hsl[1], hsl[2])};
    }

    public Component renderGradient(String text, Player player) {
        String[] colors = getGradientColors(player);
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize("<gradient:" + colors[0] + ":" + colors[1] + ">"
                + mm.escapeTags(text) + "</gradient>");
    }

    public void setColor(UUID uuid, String hex) {
        storage.setColor(uuid, hex);
    }

    public void resetColor(UUID uuid) {
        storage.removeColor(uuid);
    }

    private String generateColor(UUID uuid) {
        String entry = defaultColors.get(Math.abs(uuid.hashCode() % defaultColors.size()));
        String hex = ColorConverter.resolveHex(entry);
        return hex != null ? hex : ColorConverter.toHex(NamedTextColor.WHITE);
    }
}
