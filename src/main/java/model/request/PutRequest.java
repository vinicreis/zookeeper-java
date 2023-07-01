package model.request;

import com.google.gson.annotations.SerializedName;
import model.Request;
import model.Operation;

public class PutRequest implements Request {
    @SerializedName("host") private final String host;
    @SerializedName("port") private final int port;
    @SerializedName("key") private final String key;
    @SerializedName("value") private final String value;

    public PutRequest(String host, int port, String key, String value) {
        this.host = host;
        this.port = port;
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

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
