package model.response;

import model.Operation;
import model.Response;
import model.Result;

public class ReplicationResponse extends Response {
    private ReplicationResponse(Result result, String message, Long timestamp) {
        super(result, message, timestamp);
    }

    public static class Builder extends AbstractBuilder<ReplicationResponse> {
        @Override
        public ReplicationResponse build() {
            return new ReplicationResponse(result, message, timestamp);
        }
    }

    @Override
    public Operation getOperation() {
        return Operation.REPLICATE;
    }
}
