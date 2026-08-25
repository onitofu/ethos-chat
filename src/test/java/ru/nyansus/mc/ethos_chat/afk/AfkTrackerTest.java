package ru.nyansus.mc.ethos_chat.afk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;
import org.junit.Test;

public class AfkTrackerTest {

    private static final UUID PLAYER = UUID.fromString(
            "00000000-0000-0000-0000-000000000001");

    @Test
    public void manualToggleAndActivityReturnPlayer() {
        AfkTracker tracker = new AfkTracker();
        tracker.join(PLAYER, 1_000L);

        assertTrue(tracker.toggle(PLAYER, 2_000L));
        assertTrue(tracker.isAfk(PLAYER));
        assertTrue(tracker.recordActivity(PLAYER, 3_000L));
        assertFalse(tracker.isAfk(PLAYER));
        assertFalse(tracker.recordActivity(PLAYER, 4_000L));
    }

    @Test
    public void timeoutMarksPlayerOnlyOnce() {
        AfkTracker tracker = new AfkTracker();
        tracker.join(PLAYER, 1_000L);

        assertTrue(tracker.markIdle(300_999L, 300_000L).isEmpty());
        assertTrue(tracker.markIdle(301_000L, 300_000L).contains(PLAYER));
        assertTrue(tracker.markIdle(700_000L, 300_000L).isEmpty());
    }

    @Test
    public void movementRestartsAutomaticTimeout() {
        AfkTracker tracker = new AfkTracker();
        tracker.join(PLAYER, 0L);
        tracker.recordActivity(PLAYER, 250_000L);

        assertTrue(tracker.markIdle(300_000L, 300_000L).isEmpty());
        assertTrue(tracker.markIdle(550_000L, 300_000L).contains(PLAYER));
    }

    @Test
    public void quitRemovesSessionState() {
        AfkTracker tracker = new AfkTracker();
        tracker.join(PLAYER, 0L);
        tracker.toggle(PLAYER, 1L);
        tracker.quit(PLAYER);

        assertFalse(tracker.isAfk(PLAYER));
        assertTrue(tracker.markIdle(1_000_000L, 300_000L).isEmpty());
    }
}
