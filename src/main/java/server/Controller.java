package server;

import log.ConsoleLog;
import log.Log;
import model.request.ExitRequest;
import model.request.JoinRequest;
import model.request.Request;
import model.response.ExitResponse;
import model.response.JoinResponse;
import server.controller.ControllerImpl;
import ui.Message;

import java.util.Arrays;

import static util.AssertionUtils.handleException;
import static util.IOUtil.*;

public interface Controller extends Server {
    JoinResponse join(JoinRequest request);
    ExitResponse exit(ExitRequest request);

    class Node {
        private final String host;
        private final int port;

        public Node(Request request) {
            this.host = request.getHost();
            this.port = request.getPort();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if(!(obj instanceof Node)) return false;

            final Node other = (Node)obj;

            return other.getHost().equals(host) && other.port == port;
        }

        @Override
        public String toString() {
            return String.format("%s:%d", host, port);
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }

    static void main(String[] args) {
        try {
            final Log log = new ConsoleLog("ControllerMain");
            final boolean debug = Arrays.stream(args).anyMatch((arg) -> arg.equals("--debug") || arg.equals("-d"));
            final int port = Integer.parseInt(readWithDefault(Message.ENTER_PORT, "10097"));
            final Controller controller = new ControllerImpl(port, debug);

            log.setDebug(debug);

            log.d("Starting controller...");
            controller.start();
            log.d("Controller started!");

            pressAnyKeyToFinish();

            log.d("Finishing controller...");
            controller.stop();
            log.d("Controller finished!");
        } catch (Exception e) {
            handleException("ControllerMain", "Failed start Controller...", e);
        }
    }
}
