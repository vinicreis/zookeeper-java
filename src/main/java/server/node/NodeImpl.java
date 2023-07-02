package server.node;

import com.google.gson.Gson;
import log.ConsoleLog;
import log.Log;
import model.Operation;
import model.Result;
import model.repository.KeyValueRepository;
import model.repository.TimestampRepository;
import model.request.JoinRequest;
import model.request.PutRequest;
import model.request.ReplicationRequest;
import model.response.JoinResponse;
import model.response.PutResponse;
import model.response.ReplicationResponse;
import model.type.SocketCallable;
import model.type.SocketRunnable;
import server.Node;
import server.controller.thread.DispatcherThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static util.AssertionUtils.handleException;
import static util.IOUtil.printfLn;

public class NodeImpl implements Node {
    private static final String TAG = "NodeImpl";
    private static final Log log = new ConsoleLog(TAG);
    private static final Gson gson = new Gson();
    private final KeyValueRepository keyValueRepository;
    private final TimestampRepository timestampRepository;
    private final DispatcherThread dispatcher;
    private final ServerSocket serverSocket;
    private final String controllerHost;
    private final int controllerPort;
    private final int port;

    public NodeImpl(int port, String controllerHost, int controllerPort, boolean debug) throws IOException {
        this.port = port;
        this.controllerHost = controllerHost;
        this.controllerPort = controllerPort;
        this.serverSocket = new ServerSocket(port);
        this.dispatcher = new DispatcherThread(this, this.serverSocket);
        this.timestampRepository = new TimestampRepository();
        this.keyValueRepository = new KeyValueRepository(this.timestampRepository);

        log.setDebug(debug);
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public TimestampRepository getTimestampRepository() {
        return timestampRepository;
    }

    @Override
    public KeyValueRepository getKeyValueRepository() {
        return keyValueRepository;
    }

    @Override
    public void start() {
        timestampRepository.start();
        dispatcher.start();

        join();
    }

    @Override
    public void stop() {
        dispatcher.interrupt();
        timestampRepository.stop();
    }

    @Override
    public void join() {
        new SocketRunnable(
                controllerHost,
                controllerPort,
                ((socket, in, out) -> {
                    final JoinRequest request = new JoinRequest(
                            serverSocket.getInetAddress().getHostAddress(),
                            serverSocket.getLocalPort()
                    );

                    log.d("Sending JOIN message...");
                    out.writeUTF(Operation.JOIN.getName());
                    out.flush();
                    out.writeUTF(gson.toJson(request));
                    out.flush();

                    log.d("Waiting for JOIN response...");

                    final String jsonResponse = in.readUTF();

                    log.d(String.format("Response received: %s", jsonResponse));
                    final JoinResponse response = gson.fromJson(jsonResponse, JoinResponse.class);

                    if (response.getResult() != Result.OK) {
                        throw new RuntimeException(String.format("Failed to join on controller server: %s", response.getMessage()));
                    }

                    log.d("Node successfully joined!");
                }),
                e -> handleException(TAG, "Failed to process JOIN operation", e)
        ).run();
    }

    @Override
    public PutResponse put(PutRequest request) {
        return new SocketCallable<>(
                controllerHost,
                controllerPort,
                ((socket, in, out) -> {
                    printfLn(
                            "Encaminhando %s:%d PUT key: %s value: $s",
                            request.getHost(),
                            request.getPort(),
                            request.getKey(),
                            request.getValue()
                    );

                    log.d("Sending PUT request to Controller...");
                    out.writeUTF(Operation.PUT.getName());
                    out.flush();
                    out.writeUTF(request.toJson());
                    out.flush();

                    log.d("Waiting PUT response from Controller...");
                    final String jsonResponse = in.readUTF();
                    log.d(String.format("PUT response received: %s", jsonResponse));

                    final PutResponse controllerResponse = gson.fromJson(jsonResponse, PutResponse.class);

                    if (controllerResponse.getResult() != Result.OK) {
                        return new PutResponse.Builder()
                                .result(Result.ERROR)
                                .message(controllerResponse.getMessage())
                                .build();
                    }

                    return new PutResponse.Builder()
                            .timestamp(controllerResponse.getTimestamp())
                            .result(Result.OK)
                            .build();
                }),
                e -> {
                    handleException(TAG, "Failed to process PUT operation", e);

                    return new PutResponse.Builder().exception(e).build();
                }
        ).call();
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
            keyValueRepository.update(request.getKey(), request.getValue(), request.getTimestamp());

            return new ReplicationResponse.Builder().result(Result.OK).build();
        } catch (Exception e) {
            handleException(TAG, "Failed to process REPLICATE operation", e);

            return new ReplicationResponse.Builder().exception(e).build();
        }
    }
}
