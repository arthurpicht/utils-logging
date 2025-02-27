package de.arthurpicht.utils.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogbackInit {

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
    public LogbackInit withConsoleAppender(boolean consoleAppender) {
        this.consoleAppender = consoleAppender;
        return this;
    }

    /**
     * Replace usage with project arthurpicht/console.
     */
    @Deprecated
    public LogbackInit withConsoleAppender() {
        return withConsoleAppender(true);
    }

    /**
     * Replace usage with project arthurpicht/console.
     */
    @Deprecated
    public LogbackInit withConsoleLayout(String layoutConsole) {
        this.layoutConsole = layoutConsole;
        return this;
    }

    /**
     * Replace usage with project arthurpicht/console.
     */
    @Deprecated
    public LogbackInit withConsoleLogLevel(Level consoleLevel) {
        this.logLevelConsole = consoleLevel;
        return this;
    }

    public LogbackInit addLogFile(LogFile logFile) {
        this.logFileList.add(logFile);
        return this;
    }

    public LogbackInit addLoggerLevel(String logger, Level level) {
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


}
