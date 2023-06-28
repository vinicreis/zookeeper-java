package server.controller;

import model.request.JoinRequest;
import model.response.JoinResponse;
import server.Server;
import ui.Message;

import static util.IOUtil.read;

public interface Controller extends Server {
    JoinResponse join(JoinRequest request);

    static void main(String[] args) {
        try {
            final String host = read(Message.ENTER_HOST);
            final int port = Integer.parseInt(read(Message.ENTER_PORT));
            final Controller controller = new ControllerImpl(port);

            controller.start();

            // TODO: Find a way to finish it gracefully}
        } catch (Exception e) {

        }
    }
}
