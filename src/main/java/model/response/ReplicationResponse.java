package model.response;

import com.google.gson.annotations.SerializedName;
import model.Operation;
import model.Response;
import model.Result;

public class ReplicationResponse implements Response {
    @SerializedName("result") private final Result result;
    @SerializedName("message") private final String message;

    public ReplicationResponse() {
        this.result = Result.OK;
        this.message = null;
    }

    public ReplicationResponse(Result result) {
        this.result = result;
        this.message = null;
    }

    public ReplicationResponse(Result result, String message) {
        this.result = result;
        this.message = message;
    }

    @Override
    public Operation getOperation() {
        return Operation.REPLICATE;
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
