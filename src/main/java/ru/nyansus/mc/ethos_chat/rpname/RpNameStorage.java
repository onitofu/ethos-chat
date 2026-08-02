package ru.nyansus.mc.ethos_chat.rpname;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.UUID;

public interface RpNameStorage {

    Optional<String> getRpName(UUID uuid);

    void setRpName(UUID uuid, String name);

    void removeRpName(UUID uuid);

    Optional<String> getRace(UUID uuid);

    void setRace(UUID uuid, String race);

    void removeRace(UUID uuid);

    OptionalDouble getNametagHeight(UUID uuid);

    void setNametagHeight(UUID uuid, double height);

    void removeNametagHeight(UUID uuid);
}
