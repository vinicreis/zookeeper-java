package server;

import com.google.gson.Gson;
import log.ConsoleLog;
import log.Log;
import model.type.Pair;
import server.thread.ServerThread;
import ui.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

import static util.IOUtil.read;

public abstract class ServerImpl implements Server {
    protected final ServerSocket serverSocket;

    public ServerImpl() throws IOException {
        final int port = Integer.parseInt(read(Message.ENTER_PORT));
        this.serverSocket = new ServerSocket(port);
    }
}
