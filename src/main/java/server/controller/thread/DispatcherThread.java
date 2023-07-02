package server.controller.thread;

import log.ConsoleLog;
import log.Log;
import model.Operation;
import server.Server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;

import static util.AssertionUtils.handleException;

public class DispatcherThread extends Thread {
    private static final String TAG = "DispatcherThread";
    private final Log log = new ConsoleLog(TAG);
    private final Server server;
    private boolean running = true;

    public DispatcherThread(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        try(ServerSocket serverSocket = new ServerSocket(server.getPort())) {
            while (running) {
                log.d("Listening for operation requests...");
                final Socket socket = serverSocket.accept();
                log.d("Request received!");
                final DataInputStream reader = new DataInputStream(socket.getInputStream());

                final String operationCode = reader.readUTF();
                final String message = reader.readUTF();

                log.d("Starting Worker thread...");
                new WorkerThread(server, socket, Operation.fromCode(operationCode), message).start();
            }
        } catch (EOFException e) {
            handleException(TAG, "Invalid input received from client", e);
        } catch (Exception e) {
            handleException(TAG, "Failed during dispatch execution", e);
        }
    }

    @Override
    public void interrupt(){
        try {
            super.interrupt();
            running = false;
        } catch (Exception e) {
            handleException(TAG, "Failed while interrupting dispatcher!", e);
        }
    }
}