package ru.nyansus.mc.ethos_chat.rpname;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class YamlRpNameStorageTest {

    private static final UUID PLAYER = UUID.fromString(
            "00000000-0000-0000-0000-000000000001");

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void rpStatusPersistsAndDefaultsToInactive() throws Exception {
        File file = temporaryFolder.newFile("rpnames.yml");
        YamlRpNameStorage storage = new YamlRpNameStorage(
                file, Logger.getAnonymousLogger());

        assertFalse(storage.isRpActive(PLAYER));
        storage.setRpActive(PLAYER, true);
        assertTrue(new YamlRpNameStorage(
                file, Logger.getAnonymousLogger()).isRpActive(PLAYER));

        storage.setRpActive(PLAYER, false);
        assertFalse(new YamlRpNameStorage(
                file, Logger.getAnonymousLogger()).isRpActive(PLAYER));
    }
}
