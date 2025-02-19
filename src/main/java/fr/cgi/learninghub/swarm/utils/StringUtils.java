package fr.cgi.learninghub.swarm.utils;

public class StringUtils {

    private StringUtils() {
    }

    public static String capitalizeOnlyFirstLetter(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.toLowerCase().substring(1);
    }
}
