package server.thread;

import log.ConsoleLog;
import log.Log;
import model.enums.Operation;
import server.Server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static util.AssertionUtils.handleException;

public class DispatcherThread extends Thread {
    private static final String TAG = "DispatcherThread";
    private final Log log = new ConsoleLog(TAG);
    private final Server server;
    private final ServerSocket serverSocket;
    private boolean running = true;

    public DispatcherThread(Server server) throws IOException {
        this.server = server;
        this.serverSocket = new ServerSocket(server.getPort());
    }

    @Override
    public void run() {
        try {
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
        } catch (SocketException e) {
            log.d("Socket closed!");
        } catch (Exception e) {
            handleException(TAG, "Failed during dispatch execution", e);
        }
    }

    @Override
    public void interrupt(){
        try {
            super.interrupt();
            serverSocket.close();
            running = false;
        } catch (Exception e) {
            handleException(TAG, "Failed while interrupting dispatcher!", e);
        }
    }
}