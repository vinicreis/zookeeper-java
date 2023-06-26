package model.response;

import com.google.gson.annotations.SerializedName;
import model.Operation;
import model.Response;
import model.Result;

public class PutResponse extends Response {
    @SerializedName("timestamp")
    private final Long timestamp;

    public PutResponse(Long timestamp) {
        super();

        this.timestamp = timestamp;
    }

    public PutResponse(String message) {
        super(message);

        this.timestamp = null;
    }

    public PutResponse(Exception e) {
        super(e);

        this.timestamp = null;
    }

    public PutResponse(Result result, String message, Long timestamp) {
        setResult(result);
        setMessage(message);
        this.timestamp = timestamp;
    }

    @Override
    public Operation getOperation() {
        return Operation.PUT;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
