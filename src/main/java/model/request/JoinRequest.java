package model.request;

public class JoinRequest {
    private final int port;

    public JoinRequest(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
