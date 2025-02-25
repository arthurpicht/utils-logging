package de.arthurpicht.utils.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.arthurpicht.utils.core.assertion.MethodPreconditions.assertArgumentNotNull;

public class LoggerInit {

    public static final String ROOT_LOGGER = "ROOT";
    public static final String CONSOLE_LOGGER = "CONSOLE";
    public static final String DEFAULT_LAYOUT_FILE = "%d{\"yyyy-MM-dd'T'HH:mm:ss,SSSXXX\", UTC} [%level] %logger{10} - %msg%n";
    public static final String DEFAULT_LAYOUT_CONSOLE = "%msg%n";

    private final List<LogFile> logFileList = new ArrayList<>();
    private final Map<String, Level> loggerLevel = new HashMap<>();
    private boolean consoleAppender = false;
    private String layoutConsole = DEFAULT_LAYOUT_CONSOLE;
    private Level logLevelConsole = Level.INFO;

    /**
     * Replace usage with project arthurpicht/console.
     */
    @Deprecated
    public LoggerInit withConsoleAppender(boolean consoleAppender) {
        this.consoleAppender = consoleAppender;
        return this;
    }

    /**
     * Replace usage with project arthurpicht/console.
     */
    @Deprecated
    public LoggerInit withConsoleAppender() {
        return withConsoleAppender(true);
    }

    /**
     * Replace usage with project arthurpicht/console.
     */
    @Deprecated
    public LoggerInit withConsoleLayout(String layoutConsole) {
        this.layoutConsole = layoutConsole;
        return this;
    }

    /**
     * Replace usage with project arthurpicht/console.
     */
    @Deprecated
    public LoggerInit withConsoleLogLevel(Level consoleLevel) {
        this.logLevelConsole = consoleLevel;
        return this;
    }

    public LoggerInit addLogFile(LogFile logFile) {
        this.logFileList.add(logFile);
        return this;
    }

    public LoggerInit addLoggerLevel(String logger, Level level) {
        this.loggerLevel.put(logger, level);
        return this;
    }

    public void initialize() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoderCache patternLayoutEncoderCache = new PatternLayoutEncoderCache(loggerContext);
        for (LogFile logFile : logFileList) {
            PatternLayoutEncoder patternLayoutEncoder
                    = patternLayoutEncoderCache.getPatternLayoutEncoder(logFile.getLayout());
            FileAppender<ILoggingEvent> fileAppender
                    = LogbackUtils.initializeFileAppender(loggerContext, logFile.getPath(), patternLayoutEncoder);

            Logger logger = (Logger) LoggerFactory.getLogger(logFile.getLogger());
            logger.setLevel(logFile.getLevel());
            logger.addAppender(fileAppender);
            // detach "console" which comes from logback default BasicConfigurator
            logger.detachAppender("console");
        }

        if (consoleAppender) {

            PatternLayoutEncoder patternLayoutEncoder
                    = patternLayoutEncoderCache.getPatternLayoutEncoder(layoutConsole);
            ConsoleAppender<ILoggingEvent> consoleAppender
                    = LogbackUtils.initializeConsoleAppender(loggerContext, patternLayoutEncoder);

            Logger consoleLogger = (Logger) LoggerFactory.getLogger(CONSOLE_LOGGER);
            consoleLogger.addAppender(consoleAppender);

            consoleLogger.setLevel(logLevelConsole);
        }

        for (String logger : loggerLevel.keySet()) {
            Level level = loggerLevel.get(logger);
            loggerContext.getLogger(logger).setLevel(level);
        }

    }

    public static void consoleAndFile(Path logFile, Level logLevelFile, Level logLevelConsole) {
        assertArgumentNotNull("logFile", logFile);
        assertArgumentNotNull("logLevelFile", logLevelFile);
        assertArgumentNotNull("logLevelConsole", logLevelConsole);

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder patternLayoutEncoderFile = new PatternLayoutEncoder();
        patternLayoutEncoderFile.setPattern("%d{\"yyyy-MM-dd'T'HH:mm:ss,SSSXXX\", UTC} [%level] %logger{10} - %msg%n");
        patternLayoutEncoderFile.setContext(loggerContext);
        patternLayoutEncoderFile.start();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setFile(logFile.toAbsolutePath().toString());
        fileAppender.setEncoder(patternLayoutEncoderFile);
        fileAppender.setContext(loggerContext);
        fileAppender.start();

        PatternLayoutEncoder patternLayoutEncoderConsole = new PatternLayoutEncoder();
        patternLayoutEncoderConsole.setPattern("%msg%n");
        patternLayoutEncoderConsole.setContext(loggerContext);
        patternLayoutEncoderConsole.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setEncoder(patternLayoutEncoderConsole);
        consoleAppender.setContext(loggerContext);
        consoleAppender.start();

        Logger logger = (Logger) LoggerFactory.getLogger("ROOT");
        // detach "console" which comes from logback default BasicConfigurator
        logger.detachAppender("console");
        logger.addAppender(fileAppender);
        logger.setLevel(logLevelFile);
//        logger.setAdditive(false); /* set to true if root should log too */

        Logger consoleLogger = (Logger) LoggerFactory.getLogger(LoggerNames.CONSOLE);
        consoleLogger.addAppender(consoleAppender);
        consoleLogger.setLevel(logLevelConsole);
    }

}
