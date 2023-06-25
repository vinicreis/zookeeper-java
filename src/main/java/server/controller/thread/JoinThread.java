package server.controller.thread;

import model.request.JoinRequest;
import server.Server;

public class JoinThread extends Thread {
    private final JoinRequest request;
    private final Server server;

    public JoinThread(Server server, JoinRequest request) {
        this.request = request;
        this.server = server;
    }

    @Override
    public void run() {
        server.join(request);
    }
}
