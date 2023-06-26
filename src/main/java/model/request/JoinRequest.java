package model.request;

public class JoinRequest {
    private final String host;
    private final int port;

    public JoinRequest(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
