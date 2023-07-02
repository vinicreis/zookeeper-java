package client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static util.AssertionUtils.handleException;
import static util.IOUtil.readWithDefault;

public interface Client {
    void start();
    void stop();
    void put(String key, String value);
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
