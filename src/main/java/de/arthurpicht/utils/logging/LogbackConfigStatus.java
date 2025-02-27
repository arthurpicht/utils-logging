package de.arthurpicht.utils.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class LogbackConfigStatus {

    public static String get() {

        StringBuilder stringBuilder = new StringBuilder();

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (Logger logbackLogger : loggerContext.getLoggerList()) {
            stringBuilder
                    .append("Logger       : ").append(logbackLogger.getName()).append("\n");

            if (logbackLogger.getLevel() != null) {
                stringBuilder.append("    Level    : ").append(logbackLogger.getLevel()).append("\n");
            }

            if (hasAppenders(logbackLogger)) {
                stringBuilder.append("    Appenders:\n");
                Iterator<Appender<ILoggingEvent>> appenderIterator = logbackLogger.iteratorForAppenders();
                while (appenderIterator.hasNext()) {
                    Appender<ILoggingEvent> appender = appenderIterator.next();

                    stringBuilder.append("        ").append("Name :").append(appender.getName()).append("\n");
                    stringBuilder.append("        ").append("Type :").append(appender.getClass().getSimpleName()).append("\n");

                    if (appender.getClass().getSimpleName().equals("FileAppender")) {
                        FileAppender<ILoggingEvent> fileAppender = (FileAppender<ILoggingEvent>) appender;
                        stringBuilder.append("        ").append("File : ").append(fileAppender.getFile()).append("\n");
                    }
                }
            }
        }

        return stringBuilder.toString();
    }

    private static boolean hasAppenders(Logger logger) {
        Iterator<Appender<ILoggingEvent>> it = logger.iteratorForAppenders();
        return it.hasNext();
    }

}
