package de.arthurpicht.utils.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import de.arthurpicht.utils.io.file.TextFileUtils;
import de.arthurpicht.utils.io.nio2.FileUtils;
import de.arthurpicht.utils.io.tempDir.TempDir;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoggerInitTest {

    private static TempDir tempDir;

    @BeforeAll
    public static void setup() {
        tempDir = new TempDir.Creator()
                .withAutoRemove(false)
                .withParentDir(".")
                .withTempDirPrefix("temp-test-")
                .create();
    }

    @AfterEach
    public void resetLogBack() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
    }

    @Test
    public void defaultLogFile() throws IOException {
        Path logFilePath = tempDir.asPath().resolve("defaultLog.log");

        new LoggerInit()
                .addLogFile(new LogFile.Builder()
                        .withPath(logFilePath)
                        .build())
                .initialize();

        Logger logger = LoggerFactory.getLogger("dummy");
        logger.info("Hello World");
        logger.debug("statement on debug level");

        assertTrue(FileUtils.isExistingRegularFile(logFilePath));
        List<String> lines = TextFileUtils.readLinesAsStrings(logFilePath);
        assertFalse(lines.isEmpty());
        assertTrue(lines.getLast().endsWith("[INFO] dummy - Hello World"));
    }

    @Test
    public void logFileOnDebugLevel() throws IOException {
        Path logFilePath = tempDir.asPath().resolve("logOnDebugLevel.log");

        new LoggerInit()
                .addLogFile(new LogFile.Builder()
                        .withPath(logFilePath)
                        .withLevel(Level.DEBUG)
                        .build())
                .initialize();

        Logger logger = LoggerFactory.getLogger("dummy");
        logger.info("Hello World");
        logger.debug("statement on debug level");

        assertTrue(FileUtils.isExistingRegularFile(logFilePath));
        List<String> lines = TextFileUtils.readLinesAsStrings(logFilePath);
        assertTrue(lines.size() >= 2);
        assertTrue(lines.get(lines.size() - 2).endsWith("[INFO] dummy - Hello World"));
        assertTrue(lines.getLast().endsWith("[DEBUG] dummy - statement on debug level"));
    }

    @Test
    public void twoLogFiles() throws IOException {
        Path logFilePath = tempDir.asPath().resolve("logFileRoot.log");
        Path logFileBPath = tempDir.asPath().resolve("logFileB.log");

        new LoggerInit()
                .addLogFile(new LogFile.Builder()
                        .withPath(logFilePath)
                        .build())
                .addLogFile(new LogFile.Builder()
                        .withPath(logFileBPath)
                        .withLogger("a.b")
                        .build())
                .initialize();

        Logger logger = LoggerFactory.getLogger("dummy");
        logger.info("on dummy logger");

        Logger loggerB = LoggerFactory.getLogger("a.b");
        loggerB.info("on logger a.b");

        assertTrue(FileUtils.isExistingRegularFile(logFilePath));
        List<String> lines = TextFileUtils.readLinesAsStrings(logFilePath);
        assertTrue(lines.size() >= 2);
        assertTrue(lines.get(lines.size() - 2).endsWith("[INFO] dummy - on dummy logger"));
        assertTrue(lines.getLast().endsWith("[INFO] a.b - on logger a.b"));

        assertTrue(FileUtils.isExistingRegularFile(logFileBPath));
        lines = TextFileUtils.readLinesAsStrings(logFilePath);
        assertFalse(lines.isEmpty());
        assertTrue(lines.getLast().endsWith("[INFO] a.b - on logger a.b"));
    }

    @Test
    public void differentLogLevelForSubLogger() throws IOException {
        Path logFilePath = tempDir.asPath().resolve("logFileRoot.log");

        new LoggerInit()
                .addLogFile(new LogFile.Builder()
                        .withPath(logFilePath)
                        .withLevel(Level.DEBUG)
                        .build())
                .addLoggerLevel("a.b", Level.INFO)
                .initialize();

        Logger logger = LoggerFactory.getLogger("dummy");
        logger.info("on dummy logger");
        logger.debug("statement on debug level to dummy logger");

        Logger loggerB = LoggerFactory.getLogger("a.b");
        loggerB.info("on logger a.b");
        loggerB.debug("debug on logger a.b");

        assertTrue(FileUtils.isExistingRegularFile(logFilePath));
        List<String> lines = TextFileUtils.readLinesAsStrings(logFilePath);
        assertTrue(lines.size() >= 3);
        assertTrue(lines.get(lines.size() - 3).endsWith("[INFO] dummy - on dummy logger"));
        assertTrue(lines.get(lines.size() - 2).endsWith("[DEBUG] dummy - statement on debug level to dummy logger"));
        assertTrue(lines.getLast().endsWith("[INFO] a.b - on logger a.b"));
    }

    @Test
    public void defaultLogFileWithConsole() {
        Path logFilePath = tempDir.asPath().resolve("defaultLogWithConsole.log");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream outStream = new PrintStream(byteArrayOutputStream);
        PrintStream savedOutStream = System.out;
        System.setOut(outStream);

        new LoggerInit()
                .addLogFile(new LogFile.Builder()
                        .withPath(logFilePath)
                        .build())
                .withConsoleAppender()
                .initialize();

        Logger logger = LoggerFactory.getLogger("dummy");
        logger.info("Hello World");
        logger.debug("statement on debug level");

        Logger consoleLogger = LoggerFactory.getLogger(LoggerInit.CONSOLE_LOGGER);
        consoleLogger.info("statement on debug level to console");

        System.setOut(savedOutStream);

        String out = byteArrayOutputStream.toString();
        assertEquals("statement on debug level to console\n", out);
    }

}