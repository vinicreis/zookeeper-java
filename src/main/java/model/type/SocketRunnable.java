package model.type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

import static util.AssertionUtils.handleException;

public class SocketRunnable implements Runnable {
    private final String host;
    private final int port;
    private final OnConnect onConnect;
    private final OnException onException;

    public SocketRunnable(String host, int port, OnConnect onConnect, OnException onException) {
        this.host = host;
        this.port = port;
        this.onConnect = onConnect;
        this.onException = onException;
    }

    public interface OnConnect {
        void run(Socket socket, DataInputStream in, DataOutputStream out) throws Exception;
    }

    public interface OnException {
        void run(Exception e);
    }

    @Override
    public void run() {
        try(Socket socket = new Socket(host, port)) {
            final DataInputStream in = new DataInputStream(socket.getInputStream());
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            onConnect.run(socket, in, out);
        } catch (Exception e) {
            handleException("SocketRunnable", "Failed to run socket operation!", e);

            onException.run(e);
        }
    }
}
