package client.thread;

import client.Client;
import log.ConsoleLog;
import log.Log;
import model.enums.Operation;

import static util.AssertionUtils.handleException;
import static util.IOUtil.read;

/**
 * Client worker thread read user input and run operations.
 */
public class WorkerThread extends Thread {
    private static final String TAG = "DispatcherThread";
    private static final Log log = new ConsoleLog(TAG);
    private final Client client;
    private boolean running = true;

    public WorkerThread(Client client) {
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
                        client.get(read("Digite a chave a ser lida"));

                        break;
                    case PUT:
                        client.put(read("Digite a chave utilizada"), read("Digite o valor a ser armazenado"));

                        break;
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
