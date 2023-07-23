package io.github.vinicreis.enums;

import java.io.IOException;
import java.util.Arrays;

import static io.github.vinicreis.util.IOUtil.read;

/**
 * Enum class that represents an operation from any instance, server or client.
 */
public enum Operation {
    /**
     * Join a node to the controller.
     */
    JOIN("JOIN", 0),
    /**
     * Get a value from server using a key.
     */
    GET("GET", 1),
    /**
     * Put a value into some key.
     */
    PUT("PUT", 2),
    /**
     * Replicate the key/value pair among a controller's nodes.
     */
    REPLICATE("REPLICATE", 3),
    /**
     * Exit request to signal node is leaving controller.
     */
    EXIT("EXIT", 4);

    private final String name;
    private final int code;

    Operation(String name, int code) {
        this.name = name;
        this.code = code;
    }

    /**
     * Get the operation name to show user.
     * @return the operation name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the operation code which the user selects on console.
     * @return code to be selected.
     */
    public int getCode() {
        return code;
    }

    /**
     * Parses a code string into an Operation instance. Generally used on start of client --> server requests
     * to indicate request type.
     * @param code text code to be parsed into an operation
     * @return an operation enum instance.
     */
    public static Operation fromCode(String code) {
        switch (code) {
            case "JOIN": return JOIN;
            case "PUT": return PUT;
            case "GET": return GET;
            case "REPLICATE": return REPLICATE;
            case "EXIT": return EXIT;
            default:
                throw new IllegalArgumentException(String.format("Operation of code %s not found!", code));
        }
    }

    /**
     * Parses an operation bases on it's input code to read it from user on console.
     * @param code user input integer code.
     * @return an operation instance to indicate which operation the user is referring to.
     * @throws InterruptedException in case the user wants to shut down the server.
     */
    public static Operation fromClient(int code) throws InterruptedException {
        switch (code) {
            case 1: return GET;
            case 2: return PUT;
            default:
                throw new InterruptedException("Interrupt command from input!");
        }
    }

    /**
     * Parses any valid input from our users.
      * @return an instance operation
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException in case the user choose to interrupt the execution
     */
    public static Operation readToClient() throws IOException, InterruptedException {
        return fromClient(Integer.parseInt(read("Digite a operação desejada ou outra tecla para encerrar...\n%s: ", printToClient())));
    }

    /**
     * Build a string with all options to be selected by any client instance.
     * @return the string to be shown at the console containing all client options available.
     */
    private static String printToClient() {
        return String.join(
                " | ",
                Arrays.stream(
                        new Operation[] { GET, PUT }).map((o) -> String.format("%s [%d]", o.getName(),o.getCode())
                ).toArray(String[]::new)
        );
    }
}
