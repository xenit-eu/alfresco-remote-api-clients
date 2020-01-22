package eu.xenit.alfresco.webscriptsapi.client.spi.dto.utils;

import java.text.MessageFormat;
import java.util.regex.Pattern;

public class ContentUrlUtil {
    private static final String BASE = "(?<=({0}=))([^|]*)";
    private static final Pattern CONTENT_URL = compile("contentUrl");
    private static final Pattern MIMETYPE = compile("mimetype");
    private static final Pattern SIZE = compile("size");
    private static final Pattern ENCODING = compile("encoding");
    private static final Pattern LOCALE = compile("locale");
    private static final Pattern ID = compile("id");

    public static String getSize(String content) {
        return RegexUtil.extractPatternFromString(content, SIZE);
    }

    public static String getContentUrl(String content) {
        return RegexUtil.extractPatternFromString(content, CONTENT_URL);
    }

    public static String getMimetype(String content) {
        return RegexUtil.extractPatternFromString(content, MIMETYPE);
    }

    public static String getEncoding(String content) {
        return RegexUtil.extractPatternFromString(content, ENCODING);
    }

    public static String getLocale(String content) {
        return RegexUtil.extractPatternFromString(content, LOCALE);
    }

    public static String getId(String content) {
        return RegexUtil.extractPatternFromString(content, ID);
    }

    private static Pattern compile(String element) {
        return Pattern.compile(
                MessageFormat.format(BASE,element)
        );
    }
}