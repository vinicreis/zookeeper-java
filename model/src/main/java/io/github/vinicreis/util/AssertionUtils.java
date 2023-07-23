package io.github.vinicreis.util;

import io.github.vinicreis.log.ConsoleLog;

public class AssertionUtils {
    /**
     * Very similar to {@code assert} call. It checks if any given {@code rule} is true.
     * @param rule rule to be asserted
     * @param message {@code RuntimeException} thrown message
     * @throws RuntimeException if the rule assertion is false
     */
    public static void check(boolean rule, String message) throws RuntimeException {
        if(!rule)
            throw new RuntimeException(message);
    }

    /**
     * Checks if a {@code String} object is null or empty.
     * @param string {@code String} object to be checked
     * @return {@code true} if the string is {@code null} or empty. Otherwise, {@code false}.
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Handles exception passed to it by logging it.
     * @param tag tag to be used in the log
     * @param message message of the exception
     * @param e {@code Exception} thrown
     */
    public static void handleException(String tag, String message, Exception e) {
        new ConsoleLog(tag).e(message, e);
    }
}
