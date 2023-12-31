package io.github.vinicreis;

import io.github.vinicreis.enums.Result;
import io.github.vinicreis.log.ConsoleLog;
import io.github.vinicreis.log.Log;
import io.github.vinicreis.repository.KeyValueRepository;
import io.github.vinicreis.request.ExitRequest;
import io.github.vinicreis.request.JoinRequest;
import io.github.vinicreis.request.PutRequest;
import io.github.vinicreis.request.ReplicationRequest;
import io.github.vinicreis.response.ExitResponse;
import io.github.vinicreis.response.JoinResponse;
import io.github.vinicreis.response.PutResponse;
import io.github.vinicreis.response.ReplicationResponse;
import io.github.vinicreis.thread.DispatcherThread;

import java.io.IOException;
import java.net.InetAddress;

import static io.github.vinicreis.util.AssertionUtils.handleException;
import static io.github.vinicreis.util.IOUtil.printfLn;
import static io.github.vinicreis.util.NetworkUtil.doRequest;

public class NodeImpl implements Node {
    private static final String TAG = "NodeImpl";
    private static final Log log = new ConsoleLog(TAG);
    private final KeyValueRepository keyValueRepository;
    private final DispatcherThread dispatcher;
    private final String controllerHost;
    private final int controllerPort;
    private final int port;

    public NodeImpl(int port, String controllerHost, int controllerPort, boolean debug) throws IOException {
        this.port = port;
        this.controllerHost = controllerHost;
        this.controllerPort = controllerPort;
        this.dispatcher = new DispatcherThread(this);
        this.keyValueRepository = new KeyValueRepository();

        log.setDebug(debug);
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public KeyValueRepository getKeyValueRepository() {
        return keyValueRepository;
    }

    @Override
    public void start() {
        dispatcher.start();

        join();
    }

    @Override
    public void stop() {
        dispatcher.interrupt();

        exit();
    }

    @Override
    public void join() {
        try {
            final JoinRequest request = new JoinRequest(InetAddress.getLocalHost().getHostName(), port);
            final JoinResponse response = doRequest(controllerHost, controllerPort, request, JoinResponse.class);

            if (response.getResult() != Result.OK) {
                throw new RuntimeException(String.format("Failed to join on controller server: %s", response.getMessage()));
            }

            // TODO: Save controller info to validate REPLICATE requests

            log.d("Node successfully joined!");
        } catch (Exception e) {
            handleException(TAG, "Failed to process JOIN operation", e);
        }
    }

    @Override
    public PutResponse put(PutRequest request) {
        try {
            printfLn(
                    "Encaminhando %s:%d PUT key: %s value: %s",
                    request.getHost(),
                    request.getPort(),
                    request.getKey(),
                    request.getValue()
            );

            final PutRequest controllerRequest = new PutRequest(
                    InetAddress.getLocalHost().getHostName(),
                    port,
                    request.getKey(),
                    request.getValue()
            );
            final PutResponse controllerResponse = doRequest(
                    controllerHost,
                    controllerPort,
                    controllerRequest,
                    PutResponse.class
            );

            if (controllerResponse.getResult() != Result.OK) {
                return new PutResponse.Builder()
                        .timestamp(controllerResponse.getTimestamp())
                        .result(Result.ERROR)
                        .message(controllerResponse.getMessage())
                        .build();
            }

            return new PutResponse.Builder()
                    .timestamp(controllerResponse.getTimestamp())
                    .result(Result.OK)
                    .build();
        } catch (Exception e) {
            handleException(TAG, "Failed to process PUT operation", e);

            return new PutResponse.Builder().exception(e).build();
        }
    }

    @Override
    public ReplicationResponse replicate(ReplicationRequest request) {
        try {
            printfLn(
                    "REPLICATION key: %s value: %s ts: %d",
                    request.getKey(),
                    request.getValue(),
                    request.getTimestamp()
            );

            log.d("Saving replicated data locally...");

            keyValueRepository.replicate(request.getKey(), request.getValue(), request.getTimestamp());

            return new ReplicationResponse.Builder().result(Result.OK).build();
        } catch (Exception e) {
            handleException(TAG, "Failed to process REPLICATE operation", e);

            return new ReplicationResponse.Builder().exception(e).build();
        }
    }

    @Override
    public void exit() {
        try {
            final ExitRequest request = new ExitRequest(InetAddress.getLocalHost().getHostName(), port);

            log.d("Leaving controller...");

            final ExitResponse response = doRequest(controllerHost, controllerPort, request, ExitResponse.class);

            if (response.getResult() != Result.OK) {
                throw new RuntimeException(String.format("Failed to send EXIT request: %s", response.getMessage()));
            }

            log.d("Successfully left on controller!");
        } catch (Exception e) {
            handleException(TAG, "Failed to process EXIT request", e);
        }
    }
}
