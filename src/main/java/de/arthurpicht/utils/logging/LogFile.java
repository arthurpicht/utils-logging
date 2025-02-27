package de.arthurpicht.utils.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.nio.file.Path;

import static de.arthurpicht.utils.core.assertion.MethodPreconditions.assertArgumentNotNull;

public class LogFile {

    private final Path path;
    private final String logger;
    private final String layout;
    private final Level level;

    public static class Builder {
        private Path path;
        private String logger = Logger.ROOT_LOGGER_NAME;
        private String layout = LogbackInit.DEFAULT_LAYOUT_FILE;
        private Level level = Level.INFO;

        public Builder withPath(Path path) {
            this.path = path;
            return this;
        }

        public Builder withLogger(String logger) {
            this.logger = logger;
            return this;
        }

        public Builder withLayout(String layout) {
            this.layout = layout;
            return this;
        }

        public Builder withLevel(Level level) {
            this.level = level;
            return this;
        }

        public LogFile build() {
            return new LogFile(path, logger, layout, level);
        }

    }

    private LogFile(Path path, String logger, String layout, Level level) {
        assertArgumentNotNull("path", path);
        assertArgumentNotNull("logger", logger);
        assertArgumentNotNull("layout", layout);
        assertArgumentNotNull("level", level);
        this.path = path;
        this.logger = logger;
        this.layout = layout;
        this.level = level;
    }

    public Path getPath() {
        return path;
    }

    public String getLogger() {
        return logger;
    }

    public String getLayout() {
        return layout;
    }

    public Level getLevel() {
        return level;
    }
}
