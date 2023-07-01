package server.controller.thread;

import com.google.gson.Gson;
import log.ConsoleLog;
import log.Log;
import model.Operation;
import model.Response;
import model.request.GetRequest;
import model.request.JoinRequest;
import model.request.PutRequest;
import model.request.ReplicationRequest;
import server.Server;
import server.controller.Controller;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static util.AssertionUtils.handleException;

public class WorkerThread extends Thread {
    private static final String TAG = "WorkerThread";
    private static final Log log = new ConsoleLog(TAG);
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
            final DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

            switch (operation) {
                case JOIN:
                    if (server instanceof Controller) {
                        log.d("Processing JOIN request...");
                        response = ((Controller)server).join(gson.fromJson(request, JoinRequest.class));
                        break;
                    }

                    throw new IllegalStateException("Nodes can not handle join requests");
                case PUT:
                    response = server.put(gson.fromJson(request, PutRequest.class));
                    break;
                case REPLICATE:
                    response = server.replicate(gson.fromJson(request, ReplicationRequest.class));
                    break;
                case GET:
                    response = server.get(gson.fromJson(request, GetRequest.class));
                    break;
                default:
                    throw new IllegalStateException("Operation unknown!");
            }

            writer.writeUTF(gson.toJson(response));
            writer.flush();
            socket.close();
        } catch (Exception e) {
            handleException(TAG, "Failed during worker execution", e);
        }
    }
}
