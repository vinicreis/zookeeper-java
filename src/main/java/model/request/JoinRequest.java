package model.request;

import com.google.gson.annotations.SerializedName;
import model.enums.Operation;

/**
 * Represents a JOIN request made when a {@code Node} instance to JOIN the Controller environment.
 */
public class JoinRequest implements Request {
    @SerializedName("host") private final String host;
    @SerializedName("port") private final int port;

    public JoinRequest(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Operation getOperation() {
        return Operation.JOIN;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }
}
