package de.arthurpicht.utils.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;

import java.nio.file.Path;

public class LogbackUtils {

    public static PatternLayoutEncoder initializePatternLayoutEncoder(LoggerContext loggerContext, String layout) {
        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setPattern(layout);
        patternLayoutEncoder.setContext(loggerContext);
        patternLayoutEncoder.start();
        return patternLayoutEncoder;
    }

    public static FileAppender<ILoggingEvent> initializeFileAppender(LoggerContext loggerContext, Path path, PatternLayoutEncoder patternLayoutEncoder) {
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setFile(path.toAbsolutePath().toString());
        fileAppender.setEncoder(patternLayoutEncoder);
        fileAppender.setContext(loggerContext);
        fileAppender.start();
        return fileAppender;
    }

    public static ConsoleAppender<ILoggingEvent> initializeConsoleAppender(LoggerContext loggerContext, PatternLayoutEncoder patternLayoutEncoder) {
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setEncoder(patternLayoutEncoder);
        consoleAppender.setContext(loggerContext);
        consoleAppender.start();
        return consoleAppender;
    }

}
