package model.response;

import com.google.gson.annotations.SerializedName;
import model.Operation;
import model.Response;
import model.Result;

public class ReplicationResponse extends Response {

    public ReplicationResponse() {
        super();
    }

    public ReplicationResponse(String message) {
        super(message);
    }

    public ReplicationResponse(Exception e) {
        super(e);
    }

    public ReplicationResponse(Result result, String message) {
        this.result = result;
        this.message = message;
    }

    @Override
    public Operation getOperation() {
        return Operation.REPLICATE;
    }
}
