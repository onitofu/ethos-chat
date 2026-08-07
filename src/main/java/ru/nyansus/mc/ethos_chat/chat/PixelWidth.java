package ru.nyansus.mc.ethos_chat.chat;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public final class PixelWidth {

    private static final int DEFAULT_WIDTH = 5;
    private static final int NORMAL_SPACE = 4;
    private static final int BOLD_SPACE = 5;
    public static final int MIN_EXACT_PADDING = 12;
    private static final Map<Character, Integer> WIDTHS = new HashMap<>();

    static {
        set(1, '!', '\'', ',', '.', ':', ';', 'i', '|');
        set(2, '`', 'l');
        set(3, ' ', '"', '(', ')', '*', 'I', '[', ']', 't', '{', '}');
        set(4, '<', '>', 'f', 'k');
        set(5, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'g', 'h', 'j', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 'u', 'v', 'w', 'x', 'y',
                'z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '#', '$', '%', '&', '+', '-', '/', '=', '?', '\\',
                '^', '_');
        set(6, '@', '~');
    }

    private PixelWidth() {}

    private static void set(int width, char... chars) {
        for (char c : chars) {
            WIDTHS.put(c, width);
        }
    }

    public static int charWidth(char c) {
        return WIDTHS.getOrDefault(c, DEFAULT_WIDTH);
    }

    public static int textWidth(String text) {
        int width = 0;
        boolean skipTag = false;
        boolean bold = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '<') {
                skipTag = true;
                continue;
            }
            if (skipTag) {
                if (c == '>') {
                    skipTag = false;
                }
                continue;
            }
            if (c == '\u00a7' && i + 1 < text.length()) {
                char code = text.charAt(i + 1);
                if (code == 'l') {
                    bold = true;
                } else if (code == 'r') {
                    bold = false;
                }
                i++;
                continue;
            }
            width += charWidth(c) + 1 + (bold ? 1 : 0);
        }
        if (width > 0) {
            width--;
        }
        return width;
    }

    public static Component pad(int pixelWidth) {
        Padding padding = calculatePadding(pixelWidth);
        Component result = Component.text(" ".repeat(padding.normalSpaces));
        if (padding.boldSpaces > 0) {
            result = result.append(Component.text(" ".repeat(padding.boldSpaces))
                    .decoration(TextDecoration.BOLD, true));
        }
        return result;
    }

    static Padding calculatePadding(int pixelWidth) {
        if (pixelWidth == 0) {
            return new Padding(0, 0);
        }
        if (pixelWidth < MIN_EXACT_PADDING) {
            throw new IllegalArgumentException(
                    "Pixel padding must be zero or at least " + MIN_EXACT_PADDING);
        }
        for (int normalSpaces = 0; normalSpaces < BOLD_SPACE; normalSpaces++) {
            int remaining = pixelWidth - normalSpaces * NORMAL_SPACE;
            if (remaining >= 0 && remaining % BOLD_SPACE == 0) {
                return new Padding(normalSpaces, remaining / BOLD_SPACE);
            }
        }
        throw new IllegalArgumentException("Cannot represent pixel padding: " + pixelWidth);
    }

    record Padding(int normalSpaces, int boldSpaces) {
        int pixelWidth() {
            return normalSpaces * NORMAL_SPACE + boldSpaces * BOLD_SPACE;
        }
    }
}
