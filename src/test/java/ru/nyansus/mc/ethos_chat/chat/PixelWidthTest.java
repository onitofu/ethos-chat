package ru.nyansus.mc.ethos_chat.chat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PixelWidthTest {

    @Test
    public void everySupportedPaddingWidthIsExact() {
        for (int width = PixelWidth.MIN_EXACT_PADDING; width <= 512; width++) {
            assertEquals(width, PixelWidth.calculatePadding(width).pixelWidth());
        }
    }

    @Test
    public void nearbyNamesReachTheSameColumn() {
        int maxNameWidth = 100;
        int gap = PixelWidth.MIN_EXACT_PADDING;
        for (int nameWidth = 89; nameWidth <= maxNameWidth; nameWidth++) {
            int requestedPadding = gap + maxNameWidth - nameWidth;
            int renderedWidth = PixelWidth.calculatePadding(requestedPadding).pixelWidth();
            assertEquals(maxNameWidth + gap, nameWidth + renderedWidth);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsPaddingBelowExactRange() {
        PixelWidth.calculatePadding(PixelWidth.MIN_EXACT_PADDING - 1);
    }

    @Test
    public void stripsMiniMessageTagsFromWidth() {
        assertEquals(PixelWidth.textWidth("Test"),
                PixelWidth.textWidth("<red>Test</red>"));
    }
}
