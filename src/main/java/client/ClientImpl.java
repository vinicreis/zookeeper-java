package client;

import log.ConsoleLog;
import log.Log;
import model.enums.Result;
import model.repository.TimestampRepository;
import model.request.GetRequest;
import model.request.PutRequest;
import model.response.GetResponse;
import model.response.PutResponse;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import static util.AssertionUtils.*;
import static util.IOUtil.*;
import static util.NetworkUtil.doRequest;

public class ClientImpl implements Client {
    private static final String TAG = "ClientImpl";
    private static final Log log = new ConsoleLog(TAG);
    private final String serverHost;
    private final List<Integer> serverPorts;
    private final String host;
    private final int port;
    private final TimestampRepository timestampRepository;
    private final HashMap<String, Long> keyTimestampMap;

    public ClientImpl(int port, String serverHost, List<Integer> serverPorts, boolean debug) throws UnknownHostException {
        this.host = InetAddress.getLocalHost().getHostAddress();
        this.port = port;
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
    public void get(String key) {
        try {
            final int serverPort = getServerPort();
            final Long timestamp;

            if(keyTimestampMap.containsKey(key)) {
                timestamp = keyTimestampMap.get(key);
            } else {
                timestamp = timestampRepository.getCurrent();
            }

            final GetRequest request = new GetRequest(host, port, key, timestamp);
            final GetResponse response = doRequest(
                    serverHost,
                    serverPort,
                    request,
                    GetResponse.class
            );

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
                            serverPort,
                            request.getTimestamp(),
                            response.getTimestamp()
                    );
                    break;
                case ERROR:
                case EXCEPTION:
                default:
                    printfLn("Falha ao obter o valor da key %s: %s", key, response.getMessage());
            }
        } catch (Exception e) {
            log.e("Failed to process GET request", e);
        }
    }

    @Override
    public void put(String key, String value) {
        try {
            final int serverPort = getServerPort();

            check(!isNullOrEmpty(key), "A chave não pode ser nula ou vazia");
            check(!isNullOrEmpty(value), "O valor não pode ser nulo ou vazio");

            final PutRequest request = new PutRequest(host, port, key, value);
            final PutResponse response = doRequest(serverHost, serverPort, request, PutResponse.class);

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
                    serverHost,
                    serverPort
            );
        } catch (ConnectException e) {
            log.e(String.format("Failed connect to socket on %s:%d", host, port), e);
        } catch (IOException e) {
            log.e("Failed to run PUT operation", e);
        } catch (Exception e) {
            handleException(TAG, "Failed to complete PUT operation!", e);
        }
    }

    private int getServerPort() throws IllegalArgumentException {
        if(serverPorts.size() == 1)
            return serverPorts.get(0);
        else if (serverPorts.size() > 1)
            return serverPorts.get(new Random().nextInt(serverPorts.size() - 1));
        else throw new IllegalArgumentException("No server ports were found!");
    }
}
