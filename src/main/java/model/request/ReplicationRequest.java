package model.request;

import com.google.gson.annotations.SerializedName;
import model.enums.Operation;

/**
 * Represents a REPLICATE request made when the {@code Controller} instance
 * wants replicate a key/value pair with determined timestamp on the {@code Node} instances.
 */
public class ReplicationRequest extends Request {
    @SerializedName("key") private final String key;
    @SerializedName("value") private final String value;
    @SerializedName("timestamp") private final Long timestamp;

    public ReplicationRequest(String host, int port, String key, String value, Long timestamp) {
        super(host, port);

        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }

    @Override
    public Operation getOperation() {
        return Operation.REPLICATE;
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
     * Get the key which the {@code Controller} wants to replicate a value to.
     * @return a {@code String} with the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the value which the {@code Controller} wants to replicate.
     * @return a {@code String} with the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the timestamp which the {@code Controller} wants to replicate a value to.
     * @return a {@code Long} with the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }
}
