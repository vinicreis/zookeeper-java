package util;

import log.ConsoleLog;
import log.Log;

public class AssertionUtils {
    public static void check(boolean rule, String message) throws RuntimeException {
        if(!rule)
            throw new RuntimeException(message);
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static void handleException(String tag, String message, Exception e) {
        new ConsoleLog(tag).e(message, e);
    }
}
