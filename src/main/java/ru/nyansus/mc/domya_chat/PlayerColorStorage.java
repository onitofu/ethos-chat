package ru.nyansus.mc.domya_chat;

import java.util.Optional;
import java.util.UUID;

public interface PlayerColorStorage {

    Optional<String> getColor(UUID uuid);

    void setColor(UUID uuid, String hex);

    void removeColor(UUID uuid);
}
