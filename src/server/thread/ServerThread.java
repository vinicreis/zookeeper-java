package server.thread;

import server.model.enums.Mode;
import ui.Message;
import util.AssertionUtils;
import util.IOUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    @Override
    public void run() {
        try(ServerSocket serverSocket = init()) {
            while(true) {
                final Socket socket = serverSocket.accept();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Read line from client
                final String json = reader.readLine();
            }
        } catch (IOException e) {

        }
    }

    private ServerSocket init() throws IOException {
//        final String host = IOUtil.read(Message.ENTER_HOST);
        final int port = Integer.parseInt(IOUtil.read(Message.ENTER_PORT));
        final Mode mode = Mode.fromInt(Integer.parseInt(IOUtil.read(Message.ENTER_MODE)));

//        AssertionUtils.check(host != null && !host.isEmpty(), "IP não pode ser nulo ou vazio");
        AssertionUtils.check(port == 0, "IP não pode ser nulo ou vazio");

        return new ServerSocket(port);
    }
}
