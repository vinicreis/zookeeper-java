package client.thread;

import client.Client;
import model.Operation;

import static util.AssertionUtils.handleException;
import static util.IOUtil.read;

public class DispatcherThread extends Thread {
    private static final String TAG = "";
    private final Client client;
    private boolean running = true;

    public DispatcherThread(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            while (running) {
                final Operation operation = Operation.fromCode(read("Digite a operação desejada: "));

                switch (operation) {
                    case GET:
                        client.get();
                        break;
                    case PUT:
                        client.put();
                        break;
                    case JOIN:
                    case REPLICATE:
                    default:
                        running = false;
                        throw new IllegalArgumentException("Client should not call any option other than GET or PUT");
                }
            }
        } catch (Exception e) {
            handleException(TAG, "Failed during thread execution!", e);
        }
    }
}
