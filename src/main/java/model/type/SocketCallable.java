package model.type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

import static util.AssertionUtils.handleException;

public class SocketCallable<T> implements Callable<T> {
    private final String host;
    private final int port;
    private final OnConnect<T> onConnect;
    private final OnException<T> onException;

    public SocketCallable(String host, int port, OnConnect<T> onConnect, OnException<T> onException) {
        this.host = host;
        this.port = port;
        this.onConnect = onConnect;
        this.onException = onException;
    }

    public interface OnConnect<T> {
        T run(Socket socket, DataInputStream in, DataOutputStream out) throws Exception;
    }

    public interface OnException<T> {
        T run(Exception e);
    }

    @Override
    public T call() {
        try(Socket socket = new Socket(host, port)) {
            final DataInputStream in = new DataInputStream(socket.getInputStream());
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            return onConnect.run(socket, in, out);
        } catch (Exception e) {
            handleException("SocketCallable", "Failed to run socket operation!", e);

            return onException.run(e);
        }
    }
}
