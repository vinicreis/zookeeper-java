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

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
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

    public ClientImpl(int port, String host, String serverHost, List<Integer> serverPorts, boolean debug){
        this.port = port;
        this.host = host;
        this.serverHost = serverHost;
        this.serverPorts = serverPorts;
        this.timestampRepository = new TimestampRepository();

        log.setDebug(debug);
    }

    @Override
    public void start() {
        timestampRepository.start();
    }

    @Override
    public void stop() {
        timestampRepository.stop();
    }

    @Override
    public void get() {
        final int serverPort = getServerPort();

        try(Socket socket = new Socket(serverHost, serverPort)) {
            final String key = read("Digite a chave a ser lida");
            final DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            final DataInputStream reader = new DataInputStream(socket.getInputStream());
            final GetRequest request = new GetRequest(
                    serverHost,
                    socket.getPort(),
                    key,
                    timestampRepository.getCurrent()
            );

            writer.writeUTF(Operation.GET.getName());
            writer.flush();
            writer.writeUTF(gson.toJson(request));
            writer.flush();

            final String jsonResponse = reader.readUTF();
            log.d(String.format("GET response received: %s", jsonResponse));
            final GetResponse response = gson.fromJson(jsonResponse, GetResponse.class);

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
                    printfLn("Failed to key value with key [%s]: %s", key, response.getMessage());
                    break;
            }
        } catch (IOException e) {
            log.e("Failed to process GET request", e);
        }
    }

    @Override
    public void put() {
        // TODO: Extract strings
        try(Socket socket = new Socket(host, getServerPort())) {
            final String key = read("Digite a chave utilizada");
            final String value = read("Digite o valor a ser armazenado");

            check(!isNullOrEmpty(key), "A chave não pode ser nula ou vazia");
            check(!isNullOrEmpty(value), "O valor não pode ser nulo ou vazio");

            final DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            final DataInputStream reader = new DataInputStream(socket.getInputStream());

            log.d(
                    String.format(
                            "PUT Sending data to server %s:%d",
                            socket.getInetAddress().getHostAddress(),
                            socket.getPort()
                    )
            );

            writer.writeUTF(Operation.PUT.getName());
            writer.flush();
            writer.writeUTF(new PutRequest(host, socket.getPort(), key, value).toJson());
            writer.flush();

            final String jsonResponse = reader.readUTF();
            log.d(String.format("Response received: %s", jsonResponse));
            final PutResponse response = gson.fromJson(jsonResponse, PutResponse.class);

            if(response.getResult() != Result.OK) {
                throw new RuntimeException(String.format("PUT operation failed: %s", response.getMessage()));
            }

            printfLn(
                    "PUT_OK key: %s value: %s timestamp: %d realizada no servidor %s:%d",
                    key,
                    value,
                    response.getTimestamp(),
                    host,
                    socket.getPort()
            );
        } catch (ConnectException e) {
            log.e(String.format("Failed connect to socket on %s:%d", host, port), e);
        } catch (IOException e) {
            log.e("Failed to run PUT operation", e);
        } catch (RuntimeException e) {
            handleException(TAG, "Failed to complete PUT operation!", e);
        }
    }

    private int getServerPort() {
        if(serverPorts.size() == 1)
            return serverPorts.get(0);
        else if (serverPorts.size() > 1)
            return serverPorts.get(new Random().nextInt(serverPorts.size() - 1));
        else throw new IllegalArgumentException("No server ports were found!");
    }
}
