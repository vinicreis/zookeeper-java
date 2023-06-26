package server.controller.thread;

import log.ConsoleLog;
import log.Log;
import model.Operation;
import server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class DispatcherThread extends Thread {
    private static final String TAG = "DispatcherThread";
    private final Log log = new ConsoleLog(TAG);
    private final Server server;
    private boolean running = true;

    public DispatcherThread(Server server) throws IOException {
        this.server = server;
    }

    @Override
    public void run() {
        try(ServerSocket serverSocket = new ServerSocket(server.getPort())) {
            while (running) {
                log.d("Listening for operation requests...");
                final Socket socket = serverSocket.accept();
                log.d("Request received!");
                final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                final String operationCode = reader.readLine();
                final String message = reader.readLine();

                new WorkerThread(server, socket, Operation.fromCode(operationCode), message).start();
            }
        } catch (Exception e) {
            // TODO: Do something...
        } finally {
            // TODO: Do something...
        }
    }

    @Override
    public void interrupt(){
        try {
            super.interrupt();
            running = false;
        } catch (Exception e) {
            // TODO: Do something...
        }
    }
}