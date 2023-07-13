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

/**
 * Generic interface that represents a {@code Controller} instance of a {@code Server}.
 */
public interface Controller extends Server {
    /**
     * Handles a JOIN request from a {@code Node}
     * @param request {@code JoinRequest} instance
     * @return {@code JoinResponse} instance
     */
    JoinResponse join(JoinRequest request);

    /**
     * Handles a EXIT request from a {@code Node}
     * @param request {@code ExitRequest} instance
     * @return {@code ExitResponse} instance
     */
    ExitResponse exit(ExitRequest request);

    /**
     * Class the represents an {@code Node} instance joined to the {@code Controller}
     */
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

        /**
         * Gets the host address of joined node.
         * @return a {@code String} value with the joined node address
         */
        public String getHost() {
            return host;
        }

        /**
         * Gets the port of joined node.
         * @return a {@code int} value with the joined node port
         */
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
