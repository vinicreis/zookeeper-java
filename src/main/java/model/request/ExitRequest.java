package model.request;

import model.enums.Operation;

/**
 * Represents a EXIT request made when a {@code Node} leaves the connection with {@code Controller}
 */
public class ExitRequest implements Request {
    private final String host;
    private final int port;

    public ExitRequest(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Operation getOperation() {
        return Operation.EXIT;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public int getPort() {
        return this.port;
    }
}
