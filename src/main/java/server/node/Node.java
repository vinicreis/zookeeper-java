package server.node;

import server.Server;
import ui.Message;

import static util.IOUtil.read;

public interface Node extends Server {
    void join();

    static void main(String[] args) {
        final int port = Integer.parseInt(read(Message.ENTER_PORT));
        final String controllerHost = read("Digite o endereço do Controller");
        final int controllerPort = Integer.parseInt(read("Digite a porta do Controller"));

        try {
            final Node node = new NodeImpl(port, controllerHost, controllerPort);

            node.start();

            read("Pressione qualquer tecla para encerrar...");

            node.stop();
        } catch (Exception e) {
            // TODO: Do something...
        }
    }
}
