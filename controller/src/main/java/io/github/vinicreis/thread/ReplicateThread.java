package io.github.vinicreis.thread;

import io.github.vinicreis.Controller;
import io.github.vinicreis.enums.Result;
import io.github.vinicreis.log.ConsoleLog;
import io.github.vinicreis.log.Log;
import io.github.vinicreis.request.ReplicationRequest;
import io.github.vinicreis.response.ReplicationResponse;

import java.io.IOException;

import static io.github.vinicreis.util.AssertionUtils.handleException;
import static io.github.vinicreis.util.NetworkUtil.doRequest;

/**
 * Thread to send a REPLICATE request to nodes asynchronously.
 */
public class ReplicateThread extends Thread {
    private static final String TAG = "ReplicateThread";
    private static final Log log = new ConsoleLog(TAG);
    private volatile Result result;
    private final Controller.Node node;
    private final ReplicationRequest request;
    private final boolean debug;

    public ReplicateThread(Controller.Node node, ReplicationRequest request, boolean debug) {
        this.request = request;
        this.node = node;
        this.debug = debug;
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
                    ReplicationResponse.class,
                    debug
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

    public Controller.Node getNode() {
        return node;
    }
}
