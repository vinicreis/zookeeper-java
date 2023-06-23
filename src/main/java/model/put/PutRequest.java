package model.put;

import com.google.gson.annotations.SerializedName;
import model.Request;
import model.Operation;

public class PutRequest implements Request {
    @SerializedName("key") private final String key;
    @SerializedName("value") private final String value;

    public PutRequest(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Operation getOperation() {
        return Operation.PUT ;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
