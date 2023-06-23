package util;

public class AssertionUtils {
    public static void check(boolean rule, String message) throws RuntimeException {
        if(!rule)
            throw new RuntimeException(message);
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
