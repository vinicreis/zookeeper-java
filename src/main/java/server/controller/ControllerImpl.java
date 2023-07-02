package server.controller;

import log.ConsoleLog;
import log.Log;
import model.Result;
import model.repository.KeyValueRepository;
import model.repository.TimestampRepository;
import model.request.JoinRequest;
import model.request.PutRequest;
import model.request.ReplicationRequest;
import model.response.JoinResponse;
import model.response.PutResponse;
import model.response.ReplicationResponse;
import server.Controller;
import server.controller.thread.DispatcherThread;
import server.controller.thread.ReplicateThread;

import java.util.ArrayList;
import java.util.List;

import static util.AssertionUtils.handleException;
import static util.IOUtil.printfLn;

public class ControllerImpl implements Controller {
    private static final String TAG = "ControllerImpl";
    private static final Log log = new ConsoleLog(TAG);
    private final KeyValueRepository keyValueRepository;
    private final TimestampRepository timestampRepository;
    private final DispatcherThread dispatcher;
    private final List<Node> nodes;
    private final int port;

    public ControllerImpl(int port, boolean debug) {
        this.port = port;
        this.nodes = new ArrayList<>();
        this.dispatcher = new DispatcherThread(this);
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
        try {
            timestampRepository.start();
            dispatcher.start();
        } catch (Exception e) {
            handleException(TAG, "Failed to start Controller!", e);
        }
    }

    @Override
    public void stop() {
        try {
            timestampRepository.stop();
            dispatcher.interrupt();
        } catch (Exception e) {
            handleException(TAG, "Failed while stopping Controller", e);
        }
    }

    @Override
    public JoinResponse join(JoinRequest request) {
        try {
            log.d(String.format("Joining node %s:%d", request.getHost(), request.getPort()));
            if (nodes.stream().anyMatch((node) -> node.getHost().equals(request.getHost()) && node.getPort() == request.getPort())) {
                log.d(String.format("Node %s:%d already joined!", request.getHost(), request.getPort()));

                return new JoinResponse.Builder()
                        .timestamp(timestampRepository.getCurrent())
                        .message(String.format(
                                "Node %s:%d already joined!",
                                request.getHost(),
                                request.getPort()
                        )).build();
            }

            nodes.add(new Node(request));

            log.d(String.format("Node %s:%d joined!", request.getHost(), request.getPort()));

            return new JoinResponse.Builder()
                    .timestamp(timestampRepository.getCurrent())
                    .result(Result.OK)
                    .build();
        } catch (Exception e) {
            handleException(TAG, "Failed to process JOIN operation", e);
            return new JoinResponse.Builder().exception(e).build();
        }
    }

    @Override
    public PutResponse put(PutRequest request) {
        try {
            printfLn(
                    "Cliente %s:%d PUT key: %s value: $s",
                    request.getHost(),
                    request.getPort(),
                    request.getKey(),
                    request.getValue()
            );

            final Long timestamp = keyValueRepository.upsert(request.getKey(), request.getValue());
            final ReplicationResponse replicationResponse = replicate(
                    new ReplicationRequest(
                            request.getHost(),
                            request.getPort(),
                            request.getKey(),
                            request.getValue(),
                            timestamp
                    )
            );

            if (replicationResponse.getResult() == Result.OK)
                return new PutResponse.Builder()
                        .timestamp(timestampRepository.getCurrent())
                        .result(Result.OK)
                        .build();

            return new PutResponse.Builder()
                    .timestamp(timestampRepository.getCurrent())
                    .result(replicationResponse.getResult())
                    .message(
                            String.format(
                                    "Falha ao adicionar valor %s a chave %s",
                                    request.getValue(),
                                    request.getKey()
                            )
                    )
                    .build();
        } catch (Exception e) {
            handleException(TAG, "Failed to process PUT operation", e);

            return new PutResponse.Builder().exception(e).build();
        }
    }

    @Override
    public ReplicationResponse replicate(ReplicationRequest request) {
        try {
            final List<Node> nodesWithError = new ArrayList<>(nodes.size());

            for (final Node node : nodes) {
                final ReplicateThread replicateThread = new ReplicateThread(node, request);

                log.d(String.format("Starting replication to node %s...", node.toString()));

                replicateThread.start();
                replicateThread.join();

                if (replicateThread.getResult() != Result.OK) nodesWithError.add(node);
                log.d(String.format(
                        "Replication to node %s got result: %s",
                        node,
                        replicateThread.getResult().toString())
                );
            }

            if (nodesWithError.isEmpty()) {
                printfLn(
                        "Enviando PUT_OK ao Cliente %s:%d da key: %s ts: %d",
                        request.getHost(),
                        request.getPort(),
                        request.getKey(),
                        request.getTimestamp()
                );

                return new ReplicationResponse.Builder()
                        .timestamp(timestampRepository.getCurrent())
                        .result(Result.OK)
                        .build();
            }

            return new ReplicationResponse.Builder()
                    .result(Result.ERROR)
                    .timestamp(timestampRepository.getCurrent())
                    .message(
                            String.format(
                                    "Falha ao replicar o dado no peer no(s) servidor(s) %s",
                                    String.join(
                                            ", ",
                                            nodesWithError.stream().map(Node::toString).toArray(String[]::new)
                                    )
                            )
                    ).build();
        } catch (Exception e) {
            handleException(TAG, "Failed to process REPLICATE operation", e);

            return new ReplicationResponse.Builder().exception(e).build();
        }
    }
}
