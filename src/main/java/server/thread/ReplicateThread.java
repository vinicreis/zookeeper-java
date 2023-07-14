package server.thread;

import log.ConsoleLog;
import log.Log;
import model.enums.Result;
import model.request.ReplicationRequest;
import model.response.ReplicationResponse;
import server.Controller;

import java.io.IOException;

import static util.AssertionUtils.handleException;
import static util.NetworkUtil.doRequest;

/**
 * Thread to send a REPLICATE request to nodes asynchronously.
 */
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
        try {
            log.d(
                    String.format(
                            "Sending replication request of key %s with value %s to %s:%d",
                            request.getKey(),
                            request.getValue(),
                            node.getHost(),
                            node.getPort()
                    )
            );


            final ReplicationResponse response = doRequest(
                    node.getHost(),
                    node.getPort(),
                    request,
                    ReplicationResponse.class
            );

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
