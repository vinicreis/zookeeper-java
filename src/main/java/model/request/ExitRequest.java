package model.request;

import model.enums.Operation;

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
