package server.controller.thread;

import com.google.gson.Gson;
import log.ConsoleLog;
import log.Log;
import model.Operation;
import model.Result;
import model.request.ReplicationRequest;
import model.response.ReplicationResponse;
import server.Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static util.AssertionUtils.handleException;

public class ReplicateThread extends Thread {
    private static final String TAG = "ReplicateThread";
    private static final Log log = new ConsoleLog(TAG);
    private volatile Result result;
    private final Controller.Node node;
    private final ReplicationRequest request;

    public ReplicateThread(Controller.Node node, ReplicationRequest request) {
        this.request = request;
        this.node = node;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(node.getHost(), node.getPort())) {
            final DataInputStream reader = new DataInputStream(socket.getInputStream());
            final DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

            log.d(
                    String.format(
                            "Sending replication request of key %s with value %s to %s:%d",
                            request.getKey(),
                            request.getValue(),
                            node.getHost(),
                            node.getPort()
                    )
            );

            writer.writeUTF(Operation.REPLICATE.getName());
            writer.writeUTF(request.toJson());

            final String jsonResult = reader.readUTF();
            log.d(String.format("REPLICATE response received: %s", jsonResult));
            final ReplicationResponse response = new Gson().fromJson(jsonResult, ReplicationResponse.class);

            result = response.getResult();
        } catch (IOException e) {
            handleException(TAG, String.format("Failed during REPLICATE to node %s:%d", node.getHost(), node.getPort()), e);

            result = Result.EXCEPTION;
        }
    }

    public Result getResult() {
        return result;
    }
}
