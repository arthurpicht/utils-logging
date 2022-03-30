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

import static de.arthurpicht.utils.core.assertion.MethodPreconditions.assertArgumentNotNull;

public class LoggerInit {

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
