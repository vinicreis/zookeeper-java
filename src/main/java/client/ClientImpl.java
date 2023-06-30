package client;

import com.google.gson.Gson;
import log.ConsoleLog;
import log.Log;
import model.Operation;
import model.repository.TimestampRepository;
import model.request.GetRequest;
import model.request.PutRequest;
import model.response.GetResponse;
import model.response.PutResponse;

import java.io.*;
import java.net.Socket;
import java.util.Random;

import static util.AssertionUtils.check;
import static util.AssertionUtils.isNullOrEmpty;
import static util.IOUtil.*;

public class ClientImpl implements Client {
    private static final String TAG = "ClientImpl";
    private static final Log log = new ConsoleLog(TAG);
    private static final Gson gson = new Gson();
    private final String serverHost;
    private final Integer[] serverPorts;
    private final String host;
    private final int port;
    private final TimestampRepository timestampRepository;

    public ClientImpl(int port, String host, String serverHost) {
        this.port = port;
        this.host = host;
        this.serverHost = serverHost;
        this.serverPorts = new Integer[]{ 10097, 10098, 10099 };
        this.timestampRepository = new TimestampRepository();
    }

    public ClientImpl(int port, String host, String serverHost, Integer[] serverPorts){
        this.port = port;
        this.host = host;
        this.serverHost = serverHost;
        this.serverPorts = serverPorts;
        this.timestampRepository = new TimestampRepository();
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
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final GetRequest request = new GetRequest(key, timestampRepository.getCurrent());

            writer.write(Operation.GET.getName());
            writer.write(gson.toJson(request));

            final String jsonResponse = reader.readLine();
            log.d(String.format("GET response received: %s", jsonResponse));
            final GetResponse response = gson.fromJson(jsonResponse, GetResponse.class);

            switch (response.getResult()) {
                case OK:
                    printfLn("Value received: [%s] = %s", key, response.getValue());
                    break;
                case TRY_OTHER:
                    printfLn("No value received from server %s:%d. Please, try again later...", serverHost, serverPort);
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
        try(Socket socket = new Socket(host, port)) {
            final String key = read("Digite a chave utilizada");
            final String value = read("Digite o valor a ser armazenado");

            check(isNullOrEmpty(key), "A chave não pode ser nula ou vazia");
            check(isNullOrEmpty(value), "O valor não pode ser nulo ou vazio");

            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.write(Operation.PUT.getName());
            writer.write(new PutRequest(key, value).toJson());

            final String jsonResponse = reader.readLine();
            final PutResponse response = gson.fromJson(jsonResponse, PutResponse.class);
        } catch (IOException e) {
            log.e("Failed to run PUT operation", e);
        }
    }

    private int getServerPort() {
        return serverPorts[new Random().nextInt(serverPorts.length - 1)];
    }
}
