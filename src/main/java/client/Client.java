package client;

import client.thread.DispatcherThread;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static util.AssertionUtils.handleException;
import static util.IOUtil.readWithDefault;

public interface Client {
    void start();
    void stop();
    void put();
    void get();

    static void main(String[] args) {
        try {
            final boolean debug = Arrays.stream(args).anyMatch((arg) -> arg.equals("--d") || arg.equals("-d"));
            final String host = readWithDefault("Digite o seu host", "localhost");
            final int port = Integer.parseInt(readWithDefault("Digite a sua porta", "10090"));
            final String serverHost = readWithDefault("Digite o host do servidor", "localhost");
            final String serverPortsList = readWithDefault("Dígite as portas do servidor" ,"10097,10098,10099");
            final List<Integer> serverPorts = Arrays.stream(
                    serverPortsList.replace(" ", "").split(",")
            ).map(Integer::parseInt).collect(Collectors.toList());

            final Client client = new ClientImpl(port, host, serverHost, serverPorts, debug);

            client.start();

            new DispatcherThread(client).start();
        } catch (Exception e) {
            handleException("ClientMain", "Failed to start client!", e);
        }
    }
}
