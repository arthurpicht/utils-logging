package de.arthurpicht.utils.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public static void resetConfiguration() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
    }

    public static boolean isConfigured() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

        List<String> appenderNames = new ArrayList<>();
        for (Iterator<Appender<ILoggingEvent>> it = logger.iteratorForAppenders(); it.hasNext(); ) {
            Appender<ILoggingEvent> appender = it.next();
            if (appender.getName() == null) {
                appenderNames.add("NN");
            } else {
                appenderNames.add(appender.getName());
            }
        }

        return !(appenderNames.isEmpty() || (appenderNames.size() == 1 && appenderNames.getFirst().equals("console")));
    }

    public static String getStatusPrint() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        PrintStream oldPrintStream = System.out;
        StatusPrinter.setPrintStream(printStream);
        StatusPrinter.print(loggerContext);
        StatusPrinter.setPrintStream(oldPrintStream);
        return byteArrayOutputStream.toString();
    }

}
