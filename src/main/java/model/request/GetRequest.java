package model.request;

import com.google.gson.annotations.SerializedName;
import model.enums.Operation;

public class GetRequest implements Request {
    @SerializedName("host") private final String host;
    @SerializedName("port") private final int port;
    @SerializedName("key") private final String key;
    @SerializedName("timestamp") private final Long timestamp;

    public GetRequest(String host, int port, String key, Long timestamp) {
        this.host = host;
        this.port = port;
        this.key = key;
        this.timestamp = timestamp;
    }

    @Override
    public Operation getOperation() {
        return Operation.GET;
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

    public Long getTimestamp() {
        return timestamp;
    }
}
