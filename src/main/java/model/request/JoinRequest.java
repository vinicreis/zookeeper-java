package model.request;

import com.google.gson.annotations.SerializedName;
import model.enums.Operation;

/**
 * Represents a JOIN request made when a {@code Node} instance to JOIN the Controller environment.
 */
public class JoinRequest extends Request {
    public JoinRequest(String host, int port) {
        super(host, port);
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
