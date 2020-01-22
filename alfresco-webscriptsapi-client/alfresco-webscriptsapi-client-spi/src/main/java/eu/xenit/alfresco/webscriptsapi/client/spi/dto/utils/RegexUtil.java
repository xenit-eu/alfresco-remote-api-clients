package eu.xenit.alfresco.webscriptsapi.client.spi.dto.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    public static String extractPatternFromString(String source, Pattern compiledPattern) {
        return isNullOrEmpty(source)
                ? null
                : extractGroupFrom(source, compiledPattern);
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() < 1;
    }

    private static String extractGroupFrom(String source, Pattern compiledPattern) {
        Matcher m = compiledPattern.matcher(source);
        return m.find() ? m.group(0) : null;
    }
}