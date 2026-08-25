package ru.nyansus.mc.ethos_chat.afk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AfkTracker {

    private final Map<UUID, State> states = new HashMap<>();

    public void join(UUID uuid, long nowMillis) {
        states.put(uuid, new State(nowMillis, false));
    }

    public void quit(UUID uuid) {
        states.remove(uuid);
    }

    public boolean toggle(UUID uuid, long nowMillis) {
        State state = states.computeIfAbsent(uuid, ignored -> new State(nowMillis, false));
        state.afk = !state.afk;
        state.lastActivityMillis = nowMillis;
        return state.afk;
    }

    public boolean recordActivity(UUID uuid, long nowMillis) {
        State state = states.computeIfAbsent(uuid, ignored -> new State(nowMillis, false));
        boolean returned = state.afk;
        state.afk = false;
        state.lastActivityMillis = nowMillis;
        return returned;
    }

    public List<UUID> markIdle(long nowMillis, long timeoutMillis) {
        List<UUID> newlyAfk = new ArrayList<>();
        for (Map.Entry<UUID, State> entry : states.entrySet()) {
            State state = entry.getValue();
            if (!state.afk && nowMillis - state.lastActivityMillis >= timeoutMillis) {
                state.afk = true;
                newlyAfk.add(entry.getKey());
            }
        }
        return newlyAfk;
    }

    public boolean isAfk(UUID uuid) {
        State state = states.get(uuid);
        return state != null && state.afk;
    }

    public void clear() {
        states.clear();
    }

    private static final class State {
        private long lastActivityMillis;
        private boolean afk;

        private State(long lastActivityMillis, boolean afk) {
            this.lastActivityMillis = lastActivityMillis;
            this.afk = afk;
        }
    }
}
