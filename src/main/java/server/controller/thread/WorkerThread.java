package server.controller.thread;

import com.google.gson.Gson;
import model.Operation;
import model.Response;
import model.request.JoinRequest;
import model.request.PutRequest;
import model.request.ReplicationRequest;
import model.response.JoinResponse;
import server.Server;
import server.controller.ControllerImpl;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class WorkerThread extends Thread {
    private static final Gson gson = new Gson();
    private final Server server;
    private final Socket socket;
    private final Operation operation;
    private final String request;

    public WorkerThread(Server server, Socket socket, Operation operation, String request) {
        this.server = server;
        this.socket = socket;
        this.operation = operation;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            final Response response;
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            switch (operation) {
                case JOIN:
                    response = server.join(gson.fromJson(request, JoinRequest.class));
                    break;
                case PUT:
                    response = server.put(gson.fromJson(request, PutRequest.class));
                    break;
                case REPLICATE:
                    response = server.replicate(gson.fromJson(request, ReplicationRequest.class));
                    break;
                case GET:
                    // TODO: Not implemented yet
                    throw new RuntimeException("Not implemented yet");
//                    break;
                default:
                    throw new IllegalStateException("Operation unknown!");
            }

            writer.write(gson.toJson(response));
            socket.close();
        } catch (Exception e) {
            // TODO: Do something...
        }
    }
}
