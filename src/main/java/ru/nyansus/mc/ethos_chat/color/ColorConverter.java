package ru.nyansus.mc.ethos_chat.color;

import net.kyori.adventure.text.format.NamedTextColor;

public final class ColorConverter {

    private ColorConverter() {}

    public static String toHex(NamedTextColor color) {
        return String.format("#%06X", color.value());
    }

    public static String resolveHex(String input) {
        if (input.matches("#[0-9A-Fa-f]{6}")) {
            return input;
        }
        NamedTextColor color = NamedTextColor.NAMES.value(input.toLowerCase());
        return color != null ? toHex(color) : null;
    }

    public static float[] hexToHsl(String hex) {
        int rgb = Integer.parseInt(hex.substring(1), 16);
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float lightness = (max + min) / 2f;
        if (max == min) {
            return new float[]{0, 0, lightness};
        }
        float delta = max - min;
        float saturation = delta / (1 - Math.abs(2 * lightness - 1));
        float hue;
        if (max == r) {
            hue = ((g - b) / delta) % 6;
        } else if (max == g) {
            hue = (b - r) / delta + 2;
        } else {
            hue = (r - g) / delta + 4;
        }
        hue = hue * 60;
        if (hue < 0) hue += 360;
        return new float[]{hue, saturation, lightness};
    }

    public static String hslToHex(float hue, float saturation, float lightness) {
        float h = hue / 360f;
        float r;
        float g;
        float b;
        if (saturation == 0) {
            r = lightness;
            g = lightness;
            b = lightness;
        } else {
            float q = lightness < 0.5f
                    ? lightness * (1 + saturation)
                    : lightness + saturation - lightness * saturation;
            float p = 2 * lightness - q;
            r = hueToRgb(p, q, h + 1f / 3);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f / 3);
        }
        return String.format("#%02X%02X%02X", (int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1f / 6) return p + (q - p) * 6 * t;
        if (t < 1f / 2) return q;
        if (t < 2f / 3) return p + (q - p) * (2f / 3 - t) * 6;
        return p;
    }
}
