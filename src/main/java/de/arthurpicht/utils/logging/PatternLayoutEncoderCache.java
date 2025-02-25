package de.arthurpicht.utils.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

import java.util.HashMap;
import java.util.Map;

public class PatternLayoutEncoderCache {

    private final LoggerContext loggerContext;
    private final Map<String, PatternLayoutEncoder> layoutMap;

    public PatternLayoutEncoderCache(LoggerContext loggerContext) {
        this.loggerContext = loggerContext;
        this.layoutMap = new HashMap<>();
    }

    public PatternLayoutEncoder getPatternLayoutEncoder(String layoutPattern) {
        if (layoutMap.containsKey(layoutPattern)) {
            return layoutMap.get(layoutPattern);
        } else {
            PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
            patternLayoutEncoder.setPattern(layoutPattern);
            patternLayoutEncoder.setContext(this.loggerContext);
            patternLayoutEncoder.start();
            this.layoutMap.put(layoutPattern, patternLayoutEncoder);
            return patternLayoutEncoder;
        }
    }


}
