package server.controller;

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
import server.controller.thread.DispatcherThread;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static util.AssertionUtils.handleException;

public class ControllerImpl implements Controller {
    private static final String TAG = "ControllerImpl";
    private static final Log log = new ConsoleLog(TAG);
    private static final Gson gson = new Gson();
    private final KeyValueRepository keyValueRepository;
    private final TimestampRepository timestampRepository;
    private final DispatcherThread dispatcher;
    private final List<Node> nodes;
    private final int port;

    public ControllerImpl(int port) throws IOException {
        this.port = port;
        this.nodes = new ArrayList<>();
        this.dispatcher = new DispatcherThread(this);
        this.timestampRepository = new TimestampRepository();
        this.keyValueRepository = new KeyValueRepository(this.timestampRepository);
    }

    private static class Node {
        private final String host;
        private final int port;

        Node(JoinRequest request) {
            this.host = request.getHost();
            this.port = request.getPort();
        }

        Node(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
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
            if (nodes.stream().anyMatch((node) -> node.host.equals(request.getHost()) && node.getPort() == request.getPort())) {
                log.d(String.format("Node %s:%d already joined!", request.getHost(), request.getPort()));

                return new JoinResponse.Builder()
                        .message(String.format(
                                "Node %s:%d already joined!",
                                request.getHost(),
                                request.getPort()
                        )).build();
            }

            nodes.add(new Node(request));

            log.d(String.format("Node %s:%d joined!", request.getHost(), request.getPort()));

            return new JoinResponse.Builder().result(Result.OK).build();
        } catch (Exception e) {
            handleException(TAG, "Failed to process JOIN operation", e);
            return new JoinResponse.Builder().exception(e).build();
        }
    }

    @Override
    public PutResponse put(PutRequest request) {
        try {
            final Long timestamp = keyValueRepository.upsert(request.getKey(), request.getValue());
            final ReplicationResponse replicationResponse = replicate(
                    new ReplicationRequest(
                            request.getKey(),
                            request.getValue(),
                            timestamp
                    )
            );

            if (replicationResponse.getResult() == Result.OK)
                return new PutResponse.Builder().timestamp(timestamp).build();

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
            // TODO: Do it async
            final List<Integer> portsWithError = new ArrayList<>(nodes.size());

            for (int i = 0; i < nodes.size(); i++) {
                final Node node = nodes.get(i);

                try (Socket socket = new Socket(node.getHost(), node.getPort())) {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    writer.write(Operation.REPLICATE.getName());
                    writer.write(request.toJson());

                    final String jsonResult = reader.readLine();
                    final ReplicationResponse response = gson.fromJson(jsonResult, ReplicationResponse.class);

                    if(response.getResult() != Result.OK) portsWithError.add(i);
                } catch (IOException e) {
                    portsWithError.add(i);
                    handleException(TAG, String.format("Failed during REPLICATE to node %s:%d", node.getHost(), node.getPort()), e);
                }
            }

            if (portsWithError.isEmpty()) {
                return new ReplicationResponse.Builder().build();
            } else {
                return new ReplicationResponse.Builder()
                        .result(Result.ERROR)
                        .message(
                                String.format(
                                        "Falha ao replicar o dado no peer na porta %s",
                                        String.join(", ", (String[])portsWithError.stream().map(Object::toString).toArray())
                                )
                        ).build();
            }
        } catch (Exception e) {
            handleException(TAG, "Failed to process REPLICATE operation", e);
            return new ReplicationResponse.Builder().exception(e).build();
        }
    }
}
