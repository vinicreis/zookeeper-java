package client.thread;

import client.Client;
import log.ConsoleLog;
import log.Log;
import model.Operation;

import static util.AssertionUtils.handleException;
import static util.IOUtil.printLn;

public class DispatcherThread extends Thread {
    private static final String TAG = "DispatcherThread";
    private static final Log log = new ConsoleLog(TAG);
    private final Client client;
    private boolean running = true;

    public DispatcherThread(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        log.d("Starting dispatcher thread...");

        while (running) {
            try {
                final Operation operation = Operation.readToClient();

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
                        throw new IllegalArgumentException("Client should not call any option other than GET or PUT");
                }
            } catch (InterruptedException | NumberFormatException e) {
                running = false;

                client.stop();
            } catch (Exception e) {
                handleException(TAG, "Failed during thread execution!", e);
            }
        }
    }
}
