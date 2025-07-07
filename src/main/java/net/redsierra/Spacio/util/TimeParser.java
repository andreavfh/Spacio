package net.redsierra.Spacio.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {
    public long parseToMillis(String test) {
        Pattern pattern = Pattern.compile("(\\d+)([mhd])");
        Matcher matcher = pattern.matcher(test);
        if (matcher.matches()) {
            long value = Long.parseLong(matcher.group(1));
            char unit = matcher.group(2).charAt(0);
            if (unit == 'm') {
                return value * 60 * 1000;
            } else if (unit == 'h') {
                return value * 60 * 60 * 1000;
            } else if (unit == 'd') {
                return value * 24 * 60 * 60 * 1000;
            } else if (unit == 'w') {
                return value * 7 * 24 * 60 * 60 * 1000;
            } else {
                return value * 1000;
            }
        } else {
            throw new IllegalArgumentException("Invalid time format: " + test);
        }
    }
}
