package server.node;

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
import server.controller.thread.DispatcherThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

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

    public NodeImpl(int port, String controllerHost, int controllerPort) throws IOException {
        this.port = port;
        this.controllerHost = controllerHost;
        this.controllerPort = controllerPort;
        this.serverSocket = new ServerSocket(port);
        this.dispatcher = new DispatcherThread(this);
        this.timestampRepository = new TimestampRepository();
        this.keyValueRepository = new KeyValueRepository(this.timestampRepository);
    }

    @Override
    public void start() {
        timestampRepository.start();
        dispatcher.start();
    }

    @Override
    public void stop() {
        dispatcher.interrupt();
        timestampRepository.stop();
    }

    @Override
    public void join() {
        try(Socket socket = new Socket(controllerHost, controllerPort)) {
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final JoinRequest request = new JoinRequest(
                    serverSocket.getInetAddress().getHostAddress(),
                    serverSocket.getLocalPort()
            );

            log.d("Sending JOIN message...");
            writer.write(Operation.JOIN.getCode());
            writer.write(gson.toJson(request));

            log.d("Waiting for JOIN response...");
            final String jsonResponse = reader.readLine();
            log.d(String.format("Response received: %s", jsonResponse));
            final JoinResponse response = gson.fromJson(jsonResponse, JoinResponse.class);

            if (response.getResult() != Result.OK) {
                throw new RuntimeException(String.format("Failed to join on controller server: %s", response.getMessage()));
            }

            log.d("Node successfully joined!");
        } catch (UnknownHostException e) {
            // TODO: Do something...
            throw new RuntimeException(e);
        } catch (IOException e) {
            // TODO: Do something...
        }
    }

    @Override
    public PutResponse put(PutRequest request) {
        try(Socket socket = new Socket(controllerHost, controllerPort)) {
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            log.d("Sending PUT request to Controller...");
            writer.write(Operation.PUT.getCode());
            writer.write(request.toJson());

            log.d("Waiting PUT response from Controller...");
            final String jsonResponse = reader.readLine();
            log.d(String.format("PUT response received: %s", jsonResponse));

            final PutResponse controllerResponse = gson.fromJson(jsonResponse, PutResponse.class);

            if (controllerResponse.getResult() != Result.OK) {
                return new PutResponse.Builder()
                        .result(Result.ERROR)
                        .message(controllerResponse.getMessage())
                        .build();
            }

            log.d("Saving data locally after successful PUT request on Controller...");
            final Long timestamp = keyValueRepository.upsert(request.getKey(), request.getValue());

            return new PutResponse.Builder()
                    .timestamp(timestamp)
                    .result(Result.OK)
                    .build();
        } catch (IOException e) {
            // TODO: Do something...
            log.e("Failed to process PUT request", e);
            return new PutResponse.Builder().exception(e).build();
        }
    }

    @Override
    public ReplicationResponse replicate(ReplicationRequest request) {
        try {
            log.d("Saving replicated data locally...");
            keyValueRepository.update(request.getKey(), request.getValue(), request.getTimestamp());

            return new ReplicationResponse.Builder().result(Result.OK).build();
        } catch (Exception e) {
            log.e("Failed to process REPLICATE request");
            return new ReplicationResponse.Builder().exception(e).build();
        }
    }

    @Override
    public GetResponse get(GetRequest request) {
        return null;
    }

    @Override
    public int getPort() {
        return port;
    }
}
