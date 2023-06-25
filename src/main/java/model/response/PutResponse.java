package model.response;

import com.google.gson.annotations.SerializedName;
import model.Operation;
import model.Response;
import model.Result;

public class PutResponse implements Response {
    @SerializedName("result")
    private final Result result;

    @SerializedName("timestamp")
    private final Long timestamp;

    public PutResponse(Result result, Long timestamp) {
        this.result = result;
        this.timestamp = timestamp;
    }

    @Override
    public Operation getOperation() {
        return Operation.PUT;
    }
}
