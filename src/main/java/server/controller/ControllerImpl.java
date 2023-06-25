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
import model.response.PutResponse;
import model.response.ReplicationResponse;
import server.ServerImpl;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ControllerImpl extends ServerImpl {
    private static final String TAG = "ControllerImpl";
    private static final Log log = new ConsoleLog(TAG);
    private static final Gson gson = new Gson();
    private final KeyValueRepository keyValueRepository;
    private final TimestampRepository timestampRepository;
    private final List<Integer> nodePorts;

    public ControllerImpl() throws IOException {
        super();

        this.timestampRepository = new TimestampRepository();
        this.keyValueRepository = new KeyValueRepository(this.timestampRepository);
        this.nodePorts = new ArrayList<>();
    }

    class DispatcherThread extends Thread {
        private static final String TAG = "DispatcherThread";
        private final Log log = new ConsoleLog(TAG);

        @Override
        public void run() {
            try {
                while (true) {
                    log.d("Listening for operation requests...");
                    final Socket socket = serverSocket.accept();
                    log.d("Request received!");
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    final String operationCode = reader.readLine();
                    final String message = reader.readLine();

                    new WorkerThread(Operation.fromCode(operationCode), message).start();
                }
            } catch (Exception e) {

            }
        }
    }

    class WorkerThread extends Thread {
        private final Socket socket;
        private final Operation operation;
        private final String request;

        public WorkerThread(Socket socket, Operation operation, String request) {
            this.socket = socket;
            this.operation = operation;
            this.request = request;
        }

        @Override
        public void run() {
            switch (operation) {
                case JOIN:
                    ControllerImpl.this.join(gson.fromJson(request, JoinRequest.class));
                    break;
                case PUT:
                    ControllerImpl.this.put(gson.fromJson(request, PutRequest.class));
                    break;
                case REPLICATE:
                    ControllerImpl.this.replicate(gson.fromJson(request, ReplicationRequest.class));
                    break;
                case GET:
                    // TODO: Not implemented yet
                    break;
                default:
                    throw new IllegalStateException("Operation unknown!");
            }
        }
    }

    @Override
    public void start() {
        timestampRepository.start();
        new DispatcherThread().start();
    }

    @Override
    public void stop() {
        timestampRepository.stop();
    }

    @Override
    public void join(JoinRequest request) {
        if (nodePorts.contains(request.getPort()))
            throw new IllegalStateException(String.format("Node from port %d already joined!", request.getPort()));

        nodePorts.add(request.getPort());
    }

    @Override
    public PutResponse put(PutRequest request) {
        final Long timestamp = keyValueRepository.upsert(request.getKey(), request.getValue());

        new WorkerThread(
                Operation.REPLICATE,
                new ReplicationRequest(
                        request.getKey(),
                        request.getValue(),
                        timestamp
                ).toJson()
        ).start();
    }

    @Override
    public ReplicationResponse replicate(ReplicationRequest request) {
        try {
            final List<Integer> portsWithError = new ArrayList<>(nodePorts.size());

            for (int i = 0; i < nodePorts.size(); i++) {
                try (Socket socket = new Socket(serverSocket.getInetAddress(), nodePorts.get(i))) {
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
                                String.join(", ", portsWithError.stream().map(Object::toString).toArray())
                        ),
                );
            }
        }
    }

    @Override
    public void get() {

    }
}
