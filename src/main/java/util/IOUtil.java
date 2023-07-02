package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IOUtil {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

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

    public static String read() throws IOException {
        return reader.readLine();
    }

    public static String read(String message)  throws IOException {
        System.out.print(message + ": ");

        return reader.readLine();
    }

    public static String read(String message, Object... args)  throws IOException {
        System.out.printf(message, args);

        return reader.readLine();
    }

    public static String readWithDefault(String message, String defaultValue)  throws IOException {
        System.out.printf(message + " [%s] : ", defaultValue);

        final String read = reader.readLine();

        if (read == null || read.isEmpty() || read.equals("\n"))
            return defaultValue;
        else
            return read;
    }

    public static void pressAnyKeyToFinish() throws IOException {
        printLn("Pressione qualquer tecla para finalizar...");

        System.in.read();
    }
}
