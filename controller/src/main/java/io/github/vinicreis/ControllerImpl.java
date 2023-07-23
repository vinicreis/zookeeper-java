package io.github.vinicreis;

import io.github.vinicreis.Controller;
import io.github.vinicreis.enums.Result;
import io.github.vinicreis.log.ConsoleLog;
import io.github.vinicreis.log.Log;
import io.github.vinicreis.repository.KeyValueRepository;
import io.github.vinicreis.repository.TimestampRepository;
import io.github.vinicreis.request.ExitRequest;
import io.github.vinicreis.request.JoinRequest;
import io.github.vinicreis.request.PutRequest;
import io.github.vinicreis.request.ReplicationRequest;
import io.github.vinicreis.response.ExitResponse;
import io.github.vinicreis.response.JoinResponse;
import io.github.vinicreis.response.PutResponse;
import io.github.vinicreis.response.ReplicationResponse;
import io.github.vinicreis.thread.DispatcherThread;
import io.github.vinicreis.thread.ReplicateThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.github.vinicreis.util.AssertionUtils.handleException;
import static io.github.vinicreis.util.IOUtil.printfLn;

public class ControllerImpl implements Controller {
    private static final String TAG = "ControllerImpl";
    private static final Log log = new ConsoleLog(TAG);
    private final KeyValueRepository keyValueRepository;
    private final TimestampRepository timestampRepository;
    private final DispatcherThread dispatcher;
    private final List<Node> nodes;
    private final int port;

    public ControllerImpl(int port, boolean debug) throws IOException {
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

            if (hasNode(new Node(request))) {
                log.d(String.format("Node %s:%d already joined!", request.getHost(), request.getPort()));

                return new JoinResponse.Builder()
                        .result(Result.ERROR)
                        .message(String.format(
                                "Node %s:%d already joined!",
                                request.getHost(),
                                request.getPort()
                        )).build();
            }

            nodes.add(new Node(request));

            log.d(String.format("Node %s:%d joined!", request.getHost(), request.getPort()));

            return new JoinResponse.Builder()
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
                    "Cliente %s:%d PUT key: %s value: %s",
                    request.getHost(),
                    request.getPort(),
                    request.getKey(),
                    request.getValue()
            );

            Long timestamp = keyValueRepository.insert(request.getKey(), request.getValue());
            final ReplicationResponse replicationResponse = replicate(
                    new ReplicationRequest(
                            request.getHost(),
                            request.getPort(),
                            request.getKey(),
                            request.getValue(),
                            timestamp
                    )
            );

            /*
             * The code below is used to simulate the TRY_OTHER_SERVER_OR_LATER scenario.
             * If the client host is localhost ("host.docker.internal:10092") and the port is 10092,
             * along with the key "testing" and the value "tosol", the timestamp returned should be greater
             * than the saved one. So, when the same client tries to get the value with
             * the key "testing", the timestamp sent would be greater and the server
             * should return the TRY_OTHER_SERVER result.
             */
            if (request.getHost().equals("host.docker.internal") && request.getPort() == 10092
                    && request.getKey().equals("testing") && request.getValue().equals("tosol")){
                timestamp += 1000L;
            }

            if (replicationResponse.getResult() == Result.OK) {
                return new PutResponse.Builder()
                        .timestamp(timestamp)
                        .result(Result.OK)
                        .build();
            }

            return new PutResponse.Builder()
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
            final List<ReplicateThread> threads = new ArrayList<>(nodes.size());

            // Start all threads to join them later to process request asynchronously
            for (final Node node : nodes) {
                final ReplicateThread replicateThread = new ReplicateThread(node, request, log.isDebug());

                threads.add(replicateThread);
                log.d(String.format("Starting replication to node %s...", node.toString()));

                replicateThread.start();
            }

            // Wait for each result at once, but at least they are already being processed
            for (final ReplicateThread thread : threads) {
                thread.join();

                if (thread.getResult() != Result.OK) nodesWithError.add(thread.getNode());
                log.d(String.format(
                        "Replication to node %s got result: %s",
                        thread.getNode(),
                        thread.getResult().toString())
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
                        .result(Result.OK)
                        .build();
            }

            return new ReplicationResponse.Builder()
                    .result(Result.ERROR)
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

    @Override
    public ExitResponse exit(ExitRequest request) {
        try {
            final Node node = new Node(request);

            if (!hasNode(node))
                return new ExitResponse.Builder()
                        .result(Result.ERROR)
                        .message("Servidor %s:%d não conectado!")
                        .build();

            nodes.remove(node);

            log.d(String.format("Node %s exited!", node));

            return new ExitResponse.Builder()
                    .result(Result.OK)
                    .build();
        } catch (Exception e) {
            handleException(TAG, String.format("Failed to remove node %s:%d", request.getHost(), request.getPort()), e);

            return new ExitResponse.Builder()
                    .exception(e)
                    .build();
        }
    }

    private boolean hasNode(Node node) {
        return nodes.contains(node);
    }
}
