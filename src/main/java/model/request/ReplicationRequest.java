package model.request;

import com.google.gson.annotations.SerializedName;
import model.Operation;
import model.Request;

public class ReplicationRequest implements Request {
    @SerializedName("key") private final String key;
    @SerializedName("value") private final String value;
    @SerializedName("timestamp") private final Long timestamp;

    public ReplicationRequest(String key, String value) {
        this.key = key;
        this.value = value;
        this.timestamp = null;
    }

    public ReplicationRequest(String key, String value, Long timestamp) {
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }

    @Override
    public Operation getOperation() {
        return Operation.REPLICATE;
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
