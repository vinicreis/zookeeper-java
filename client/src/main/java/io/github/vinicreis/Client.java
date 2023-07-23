package io.github.vinicreis;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.vinicreis.util.AssertionUtils.handleException;
import static io.github.vinicreis.util.IOUtil.readWithDefault;

/**
 * Client interface that will read and fetch data from servers.
 */
public interface Client {
    /**
     * Starts the client execution.
     */
    void start();

    /**
     * Stops the client execution.
     */
    void stop();

    /**
     * Sends a request to input a value with the key.
     * @param key key to input the value in
     * @param value value to be inserted
     */
    void put(String key, String value);

    /**
     * Send a request to server to read a value by key.
     * @param key key to try to read a value from.
     */
    void get(String key);

    static void main(String[] args) {
        try {
            final boolean debug = Arrays.stream(args).anyMatch((arg) -> arg.equals("--debug") || arg.equals("-d"));
            final int port = Integer.parseInt(readWithDefault("Digite a sua porta", "10090"));
            final String serverHost = readWithDefault("Digite o host do servidor", "localhost");
            final String serverPortsList = readWithDefault("Dígite as portas do servidor" ,"10097,10098,10099");
            final List<Integer> serverPorts = Arrays.stream(
                    serverPortsList.replace(" ", "").split(",")
            ).map(Integer::parseInt).collect(Collectors.toList());

            final Client client = new ClientImpl(port, serverHost, serverPorts, debug);

            client.start();
        } catch (Exception e) {
            handleException("ClientMain", "Failed to start client!", e);
        }
    }
}
