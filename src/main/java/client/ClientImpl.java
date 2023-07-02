package client;

import com.google.gson.Gson;
import log.ConsoleLog;
import log.Log;
import model.Operation;
import model.Result;
import model.repository.TimestampRepository;
import model.request.GetRequest;
import model.request.PutRequest;
import model.response.GetResponse;
import model.response.PutResponse;
import model.type.SocketRunnable;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import static util.AssertionUtils.*;
import static util.IOUtil.*;

public class ClientImpl implements Client {
    private static final String TAG = "ClientImpl";
    private static final Log log = new ConsoleLog(TAG);
    private static final Gson gson = new Gson();
    private final String serverHost;
    private final List<Integer> serverPorts;
    private final String host;
    private final int port;
    private final TimestampRepository timestampRepository;
    private final HashMap<String, Long> keyTimestampMap;

    public ClientImpl(int port, String host, String serverHost, List<Integer> serverPorts, boolean debug){
        this.port = port;
        this.host = host;
        this.serverHost = serverHost;
        this.serverPorts = serverPorts;
        this.timestampRepository = new TimestampRepository();
        this.keyTimestampMap = new LinkedHashMap<>();

        log.setDebug(debug);
    }

    @Override
    public void start() {
        timestampRepository.start();
    }

    @Override
    public void stop() {
        printLn("Encerrando...");
        timestampRepository.stop();
    }

    @Override
    public void get() {
        new SocketRunnable(
                serverHost,
                getServerPort(),
                (socket, in, out) -> {
                    final String key = read("Digite a chave a ser lida");
                    final Long timestamp;

                    if(keyTimestampMap.containsKey(key)) {
                        timestamp = keyTimestampMap.get(key);
                    } else {
                        timestamp = timestampRepository.getCurrent();
                    }

                    final GetRequest request = new GetRequest(
                            serverHost,
                            socket.getPort(),
                            key,
                            timestamp
                    );

                    out.writeUTF(Operation.GET.getName());
                    out.flush();
                    out.writeUTF(gson.toJson(request));
                    out.flush();

                    final String jsonResponse = in.readUTF();
                    log.d(String.format("GET response received: %s", jsonResponse));
                    final GetResponse response = gson.fromJson(jsonResponse, GetResponse.class);

                    timestampRepository.update(request.getTimestamp());
                    keyTimestampMap.put(key, response.getTimestamp());

                    switch (response.getResult()) {
                        case OK:
                        case TRY_OTHER:
                            printfLn(
                                    "GET_%s key: %s value: %s realizada no servidor %s:%d, meu timestamp %d e do servidor %d",
                                    response.getResult(),
                                    key,
                                    response.getValue(),
                                    serverHost,
                                    socket.getPort(),
                                    request.getTimestamp(),
                                    response.getTimestamp()
                            );
                            break;
                        case ERROR:
                        case EXCEPTION:
                        default:
                            printfLn("Falha ao obter o valor da key %s: %s", key, response.getMessage());
                    }
                },
                e -> log.e("Failed to process GET request", e)
        ).run();
    }

    @Override
    public void put() {
        // TODO: Extract strings
        new SocketRunnable(
                host,
                getServerPort(),
                ((socket, in, out) -> {
                        final String key = read("Digite a chave utilizada");
                        final String value = read("Digite o valor a ser armazenado");

                        check(!isNullOrEmpty(key), "A chave não pode ser nula ou vazia");
                        check(!isNullOrEmpty(value), "O valor não pode ser nulo ou vazio");

                        log.d(
                                String.format(
                                        "PUT Sending data to server %s:%d",
                                        socket.getInetAddress().getHostAddress(),
                                        socket.getPort()
                                )
                        );

                        out.writeUTF(Operation.PUT.getName());
                        out.flush();
                        out.writeUTF(new PutRequest(host, socket.getPort(), key, value).toJson());
                        out.flush();

                        final String jsonResponse = in.readUTF();
                        log.d(String.format("Response received: %s", jsonResponse));
                        final PutResponse response = gson.fromJson(jsonResponse, PutResponse.class);

                        if(response.getResult() != Result.OK) {
                            throw new RuntimeException(String.format("PUT operation failed: %s", response.getMessage()));
                        }

                        timestampRepository.update(response.getTimestamp());
                        keyTimestampMap.put(key, response.getTimestamp());

                        printfLn(
                                "PUT_OK key: %s value: %s timestamp: %d realizada no servidor %s:%d",
                                key,
                                value,
                                response.getTimestamp(),
                                host,
                                socket.getPort()
                        );
                }),
                e -> {
                        if (e instanceof ConnectException) {
                            log.e(String.format("Failed connect to socket on %s:%d", host, port), e);
                        } else if (e instanceof IOException) {
                            log.e("Failed to run PUT operation", e);
                        } else {
                            handleException(TAG, "Failed to complete PUT operation!", e);
                        }
                }
        ).run();
    }

    private int getServerPort() throws IllegalArgumentException {
        if(serverPorts.size() == 1)
            return serverPorts.get(0);
        else if (serverPorts.size() > 1)
            return serverPorts.get(new Random().nextInt(serverPorts.size() - 1));
        else throw new IllegalArgumentException("No server ports were found!");
    }
}
