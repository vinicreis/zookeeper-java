package io.github.vinicreis.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IOUtil {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Prints a message and jump a new line at the end.
     * @param message message to be printed on the console.
     */
    public static void printLn(String message) {
        if(message != null && !message.isEmpty())
            System.out.println(message);
    }

    /**
     * Formats and prints a message in the console.
     * @param message message to be printed
     * @param args format args to format received message.
     */
    public static void printf(String message, Object... args) {
        if(message != null && !message.isEmpty())
            System.out.printf(message, args);
    }

    /**
     * Formats and prints a messa in the console with a new line at the end.
     * @param message message to be printed
     * @param args format args to format received message
     */
    public static void printfLn(String message, Object... args) {
        if(message != null && !message.isEmpty())
            System.out.printf(message + "\n", args);
    }

    /**
     * Reads a new line from console.
     * @return a {@code string} object with the read line on the console.
     * @throws IOException if an I/O error occurs while reading a line from console
     */
    public static String read() throws IOException {
        return reader.readLine();
    }

    /**
     * Reads a line from console with an introduction message presented first.
     * @param message message to be presented to the user.
     * @return a {@code string} object with the read line on the console.
     * @throws IOException if an I/O error occurs while reading a line from console
     */
    public static String read(String message)  throws IOException {
        System.out.print(message + ": ");

        return reader.readLine();
    }

    /**
     * Reads a line from console with a formatted introduction message presented first.
     * @param message message to be presented to the user.
     * @param args format args to format the {@code message}
     * @return a {@code string} object with the read line on the console.
     * @throws IOException if an I/O error occurs while reading a line from console
     */
    public static String read(String message, Object... args)  throws IOException {
        System.out.printf(message, args);

        return reader.readLine();
    }

    /**
     * Reads a line from console with an introduction message presented first.
     * Returns the {@code defaultValue} if no input is read from the user.
     * @param message message to be presented to the user.
     * @return a {@code string} object with the read line on the console or the default value.
     * @throws IOException if an I/O error occurs while reading a line from console
     */
    public static String readWithDefault(String message, String defaultValue)  throws IOException {
        System.out.printf(message + " [%s] : ", defaultValue);

        final String read = reader.readLine();

        if (read == null || read.isEmpty() || read.equals("\n"))
            return defaultValue;
        else
            return read;
    }

    /**
     * Blocks the execution until the user press any key while displaying a message
     * to the user.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void pressAnyKeyToFinish() throws IOException {
        printLn("Pressione qualquer tecla para finalizar...");

        System.in.read();
    }
}
