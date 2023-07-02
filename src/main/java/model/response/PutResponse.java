package model.response;

import model.Operation;
import model.Response;
import model.Result;

public class PutResponse extends Response {
    private PutResponse(Result result, String message, Long timestamp) {
        super(result, message, timestamp);
    }

    public static class Builder extends AbstractBuilder<PutResponse> {
        @Override
        public PutResponse build() {
            return new PutResponse(result, message, timestamp);
        }
    }

    @Override
    public Operation getOperation() {
        return Operation.PUT;
    }
}
