package server.node;

import server.Server;
import ui.Message;

import static util.AssertionUtils.handleException;
import static util.IOUtil.read;
import static util.IOUtil.readWithDefault;

public interface Node extends Server {
    void join();

    static void main(String[] args) {
        try {
            final int port = Integer.parseInt(readWithDefault(Message.ENTER_PORT, "10098"));
            final String controllerHost = readWithDefault("Digite o endereço do Controller", "localhost");
            final int controllerPort = Integer.parseInt(readWithDefault("Digite a porta do Controller", "10097"));
            final Node node = new NodeImpl(port, controllerHost, controllerPort);

            System.out.println("Iniciando node...");
            node.start();
            System.out.println("Node iniciado...");

            read("Pressione qualquer tecla para encerrar...");

            System.out.println("Finalizando node...");
            node.stop();
            System.out.println("Node finalizado...");
        } catch (Exception e) {
            handleException("NodeMain", "Failed to initialize Node", e);
        }
    }
}
