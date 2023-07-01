package server.controller;

import log.ConsoleLog;
import log.Log;
import model.request.JoinRequest;
import model.response.JoinResponse;
import server.Server;
import ui.Message;

import java.util.Arrays;

import static util.AssertionUtils.handleException;
import static util.IOUtil.read;
import static util.IOUtil.readWithDefault;

public interface Controller extends Server {
    JoinResponse join(JoinRequest request);

    static void main(String[] args) {
        try {
            final Log log = new ConsoleLog("ControllerMain");
            final boolean debug = Arrays.stream(args).anyMatch((arg) -> arg.equals("--d") || arg.equals("-d"));
            final int port = Integer.parseInt(readWithDefault(Message.ENTER_PORT, "10097"));
            final Controller controller = new ControllerImpl(port, debug);

            log.setDebug(debug);

            log.d("Starting controller...");
            controller.start();
            log.d("Controller started!");

            read("Pressione qualquer tecla para finalizar...");

            log.d("Finishing controller...");
            controller.stop();
            log.d("Controller finished!");
        } catch (Exception e) {
            handleException("ControllerMain", "Failed start Controller...", e);
        }
    }
}
