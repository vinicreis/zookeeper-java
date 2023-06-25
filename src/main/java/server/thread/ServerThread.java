package server.thread;

import com.google.gson.Gson;
import model.request.PutRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class ServerThread extends Thread {
    private static final Gson gson = new Gson();
    private final ServerSocket serverSocket;

    public ServerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            while(true) {
                final Socket socket = serverSocket.accept();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Read line from client
                final String type = reader.readLine();
                final String json = reader.readLine();

                switch (type) {
                    case "PUT":
                        put(gson.fromJson(json, PutRequest.class));
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Operation type %s unknown!", type));
                }
            }
        } catch (IOException e) {

        }
    }

    protected abstract void put(PutRequest putRequest);
}
