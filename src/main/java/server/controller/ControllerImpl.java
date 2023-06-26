package server.controller;

import com.google.gson.Gson;
import log.ConsoleLog;
import log.Log;
import model.Operation;
import model.Result;
import model.repository.KeyValueRepository;
import model.repository.TimestampRepository;
import model.request.GetRequest;
import model.request.JoinRequest;
import model.request.PutRequest;
import model.request.ReplicationRequest;
import model.response.GetResponse;
import model.response.JoinResponse;
import model.response.PutResponse;
import model.response.ReplicationResponse;
import server.Server;
import server.controller.thread.DispatcherThread;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static util.IOUtil.read;

public class ControllerImpl implements Server {
    private static final String TAG = "ControllerImpl";
    private static final Log log = new ConsoleLog(TAG);
    private static final Gson gson = new Gson();
    private final KeyValueRepository keyValueRepository;
    private final TimestampRepository timestampRepository;
    private final List<Node> nodes;
    private final DispatcherThread dispatcher;
    private final int port;

    public ControllerImpl(int port) throws IOException {
        this.port = port;
        this.timestampRepository = new TimestampRepository();
        this.keyValueRepository = new KeyValueRepository(this.timestampRepository);
        this.nodes = new ArrayList<>();
        this.dispatcher = new DispatcherThread(this);
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
    public void start() {
        try {
            timestampRepository.start();
            dispatcher.start();
        } catch (Exception e) {
            // TODO: Do something
        }
    }

    @Override
    public void stop() {
        try {
            timestampRepository.stop();
            dispatcher.interrupt();
        } catch (Exception e) {
            // TODO: Do something
            throw new RuntimeException(e);
        }
    }

    @Override
    public JoinResponse join(JoinRequest request) {
        try {
            if (nodes.stream().anyMatch((node) -> node.host.equals(request.getHost()) && node.getPort() == request.getPort()))
                return JoinResponse.builder()
                        .message(String.format(
                                "Node %s:%d already joined!",
                                request.getHost(),
                                request.getPort()
                        )).build();

            nodes.add(new Node(request));

            return JoinResponse.builder().result(Result.OK).build();
        } catch (Exception e) {
            // TODO: Do something...
            return JoinResponse.builder().exception(e).build();
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
                return new PutResponse(timestamp);

            return new PutResponse(
                    String.format(
                            "Falha ao adicionar valor %s a chave %s",
                            request.getValue(),
                            request.getKey()
                    )
            );
        } catch (Exception e) {
            // TODO: Do something
            return new PutResponse(e);
        }
    }

    @Override
    public ReplicationResponse replicate(ReplicationRequest request) {
        try {
            final List<Integer> portsWithError = new ArrayList<>(nodes.size());

            for (int i = 0; i < nodes.size(); i++) {
                final Node node = nodes.get(i);

                try (Socket socket = new Socket(node.getHost(), node.getPort())) {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    writer.write(Operation.REPLICATE.getCode());
                    writer.write(request.toJson());

                    final String jsonResult = reader.readLine();
                    final ReplicationResponse response = gson.fromJson(jsonResult, ReplicationResponse.class);

                    if(response.getResult() != Result.OK) portsWithError.add(i);
                } catch (IOException e) {
                    portsWithError.add(i);
                    // TODO: Do something
                }
            }

            if (portsWithError.isEmpty()) {
                return new ReplicationResponse();
            } else {
                return new ReplicationResponse(
                        Result.ERROR,
                        String.format(
                                "Falha ao replicar o dado no peer na porta %s",
                                String.join(", ", (String[])portsWithError.stream().map(Object::toString).toArray())
                        )
                );
            }
        } catch (Exception e) {
            // TODO: Do something
            return new ReplicationResponse(e);
        }
    }

    @Override
    public GetResponse get(GetRequest request) {
        try {

        } catch (Exception e) {
            // TODO: Do something
            return GetResponse.builder().exception(e).build();
        }
    }
}
