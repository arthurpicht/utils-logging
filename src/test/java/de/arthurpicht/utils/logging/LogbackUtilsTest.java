package de.arthurpicht.utils.logging;

import de.arthurpicht.utils.io.tempDir.TempDir;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogbackUtilsTest {

    private static TempDir tempDir;

    @BeforeAll
    public static void setup() {
        tempDir = new TempDir.Creator()
                .withAutoRemove(true)
                .withParentDir(".")
                .withTempDirPrefix("temp-test-")
                .create();
    }

    @AfterEach
    public void resetLogBack() {
        LogbackUtils.resetConfiguration();
    }

    @Test
    public void isConfigured_cleanState() {
        assertFalse(LogbackUtils.isConfigured());
    }

    @Test
    public void isConfigured_uncleanState() {
        Path logFilePath = tempDir.asPath().resolve("some.log");
        new LogbackInit()
                .addLogFile(new LogFile.Builder()
                        .withPath(logFilePath)
                        .build())
                .initialize();

        assertTrue(LogbackUtils.isConfigured());
    }

}
