package client;

import client.thread.DispatcherThread;
import log.ConsoleLog;

import java.net.Socket;
import java.util.Arrays;

import static util.IOUtil.read;

public interface Client {
    void start();
    void stop();
    void put();
    void get();

    static void main(String[] args) {
        try {
            final String host = read("Digite o seu host");
            final int port = Integer.parseInt(read("Digite a sua porta"));
            final String serverHost = read("Digite o host do servidor");
            final String serverPortsList = read("Dígite as portas do servidor (\"10097,10098,...,10999\")");
            final Integer[] serverPorts;

            if (serverPortsList == null || serverPortsList.isEmpty()) {
                serverPorts = new Integer[] { 10097, 10098, 10099 };
            } else {
                serverPorts = (Integer[])Arrays.stream(serverPortsList.replace(" ", "").split(",")).map(Integer::parseInt).toArray();
            }

            final Client client = new ClientImpl(port, host, serverHost, serverPorts);

            client.start();

            new DispatcherThread(client).start();
        } catch (Exception e) {

        }
    }
}
