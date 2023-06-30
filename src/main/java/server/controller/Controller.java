package server.controller;

import log.ConsoleLog;
import log.Log;
import model.request.JoinRequest;
import model.response.JoinResponse;
import server.Server;
import ui.Message;

import static util.AssertionUtils.handleException;
import static util.IOUtil.read;
import static util.IOUtil.readWithDefault;

public interface Controller extends Server {
    JoinResponse join(JoinRequest request);

    static void main(String[] args) {
        try {
            final String host = readWithDefault(Message.ENTER_HOST, "localhost");
            final int port = Integer.parseInt(readWithDefault(Message.ENTER_PORT, "10097"));
            final Controller controller = new ControllerImpl(port);

            controller.start();

            read("Pressione qualquer tecla para finalizar...");

            controller.stop();
        } catch (Exception e) {
            handleException("ControllerMain", "Failed start Controller...", e);
        }
    }
}
