package server;

import log.ConsoleLog;
import log.Log;
import server.node.NodeImpl;
import ui.Message;

import java.util.Arrays;

import static util.AssertionUtils.handleException;
import static util.IOUtil.read;
import static util.IOUtil.readWithDefault;

public interface Node extends Server {
    void join();

    static void main(String[] args) {
        try {
            final Log log = new ConsoleLog("NodeMain");
            final boolean debug = Arrays.stream(args).anyMatch((arg) -> arg.equals("--d") || arg.equals("-d"));
            final int port = Integer.parseInt(readWithDefault(Message.ENTER_PORT, "10098"));
            final String controllerHost = readWithDefault("Digite o endereço do Controller", "localhost");
            final int controllerPort = Integer.parseInt(readWithDefault("Digite a porta do Controller", "10097"));
            final Node node = new NodeImpl(port, controllerHost, controllerPort, debug);

            log.setDebug(debug);

            log.d("Starting node...");
            node.start();
            log.d("Node running...");

            read("Pressione qualquer tecla para encerrar...");

            log.d("Finishing node...");
            node.stop();
            log.d("Node finished!");
        } catch (Exception e) {
            handleException("NodeMain", "Failed to initialize Node", e);
        }
    }
}
