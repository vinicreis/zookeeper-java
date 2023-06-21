package util;

public class IOUtil {
    public static void print(String message) {
        if(message != null && !message.isEmpty())
            System.out.print(message);
    }

    public static void printLn(String message) {
        if(message != null && !message.isEmpty())
            System.out.println(message);
    }

    public static void printfLn(String message, Object... args) {
        if(message != null && !message.isEmpty())
            System.out.printf(message + "\n", args);
    }

    public static String read() {
        return System.console().readLine();
    }

    public static String read(String message) {
        return System.console().readLine(message + ": ");
    }

    public static String read(String message, Object... args) {
        return System.console().readLine(message + ": ", args);
    }
}
