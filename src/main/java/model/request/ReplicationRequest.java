package model.request;

import com.google.gson.annotations.SerializedName;
import model.enums.Operation;

public class ReplicationRequest implements Request {
    @SerializedName("host") private final String host;
    @SerializedName("port") private final int port;
    @SerializedName("key") private final String key;
    @SerializedName("value") private final String value;
    @SerializedName("timestamp") private final Long timestamp;

    public ReplicationRequest(String host, int port, String key, String value, Long timestamp) {
        this.host = host;
        this.port = port;
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

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
