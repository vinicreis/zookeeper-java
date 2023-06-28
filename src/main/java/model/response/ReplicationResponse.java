package model.response;

import com.google.gson.annotations.SerializedName;
import model.Operation;
import model.Response;
import model.Result;

public class ReplicationResponse extends Response {
    private ReplicationResponse(Result result, String message) {
        super(result, message);
    }

    public static class Builder extends AbstractBuilder<ReplicationResponse> {
        @Override
        public ReplicationResponse build() {
            return new ReplicationResponse(result, message);
        }
    }

    @Override
    public Operation getOperation() {
        return Operation.REPLICATE;
    }
}
