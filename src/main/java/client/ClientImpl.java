package client;

import com.google.gson.Gson;
import log.ConsoleLog;
import log.Log;
import model.put.PutRequest;
import model.Operation;
import model.put.PutResponse;
import ui.Message;
import util.IOUtil;

import java.io.*;
import java.net.Socket;

import static util.AssertionUtils.check;
import static util.AssertionUtils.isNullOrEmpty;

public class ClientImpl {
    private static final String TAG = "ClientImpl";
    private static final Log log = new ConsoleLog(TAG);
    private static final Gson gson = new Gson();
    private final String host;
    private final int port;
    private long timestamp = 0;

    public ClientImpl() {
        this.host = IOUtil.read(Message.ENTER_HOST);
        this.port = Integer.parseInt(IOUtil.read(Message.ENTER_PORT));
    }

    public void put() {
        // TODO: Extract strings
        final String key = IOUtil.read("Digite a chave utilizada");
        final String value = IOUtil.read("Digite o valor a ser armazenado");

        check(isNullOrEmpty(key), "A chave não pode ser nula ou vazia");
        check(isNullOrEmpty(value), "O valor não pode ser nulo ou vazio");

        try(Socket socket = new Socket(host, port)) {
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.write(Operation.PUT.getCode());
            writer.write(new PutRequest(key, value).toJson());

            final String jsonResponse = reader.readLine();
            final PutResponse response = gson.fromJson(jsonResponse, PutResponse.class);
        } catch (IOException e) {
            log.e("Failed to run PUT operation", e);
        }
    }
}
