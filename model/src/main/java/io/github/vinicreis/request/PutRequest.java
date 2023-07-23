package io.github.vinicreis.request;

import com.google.gson.annotations.SerializedName;
import io.github.vinicreis.enums.Operation;

/**
 * Represents a PUT request made when a {@code Client} instance wants to insert
 * a value in a determined key.
 */
public class PutRequest extends Request {
    @SerializedName("key") private final String key;
    @SerializedName("value") private final String value;

    public PutRequest(String host, int port, String key, String value) {
        super(host, port);

        this.key = key;
        this.value = value;
    }

    @Override
    public Operation getOperation() {
        return Operation.PUT ;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    /**
     * Get the key which the client wants to add a value to.
     * @return a {@code String} with the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the value which the client wants to add.
     * @return a {@code String} with the value
     */
    public String getValue() {
        return value;
    }
}
