package io.github.vinicreis;

import io.github.vinicreis.log.ConsoleLog;
import io.github.vinicreis.log.Log;

import java.util.Arrays;

import static io.github.vinicreis.util.AssertionUtils.handleException;
import static io.github.vinicreis.util.IOUtil.pressAnyKeyToFinish;
import static io.github.vinicreis.util.IOUtil.readWithDefault;

/**
 * Generic interface that represents {@code Node} instance of a {@code Server}
 */
public interface Node extends Server {
    /**
     * Trigger the JOIN process from a {@code Node} to a {@code Controller}
     */
    void join();

    /**
     * Trigger the EXIT process from a {@code Node} to a {@code Controller}
     */
    void exit();

    static void main(String[] args) {
        try {
            final Log log = new ConsoleLog("NodeMain");
            final boolean debug = Arrays.stream(args).anyMatch((arg) -> arg.equals("--debug") || arg.equals("-d"));
            final int port = Integer.parseInt(readWithDefault("Digite a porta do servidor", "10098"));
            final String controllerHost = readWithDefault("Digite o endereço do Controller", "localhost");
            final int controllerPort = Integer.parseInt(readWithDefault("Digite a porta do Controller", "10097"));
            final Node node = new NodeImpl(port, controllerHost, controllerPort, debug);

            log.setDebug(debug);

            log.d("Starting node...");
            node.start();
            log.d("Node running...");

            pressAnyKeyToFinish();

            log.d("Finishing node...");
            node.stop();
            log.d("Node finished!");
        } catch (Exception e) {
            handleException("NodeMain", "Failed to initialize Node", e);
        }
    }
}
