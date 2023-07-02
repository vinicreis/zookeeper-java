package model.response;

import model.enums.Operation;
import model.enums.Result;

public class ExitResponse extends Response {
    private ExitResponse(Result result, String message, Long timestamp) {
        super(result, message, timestamp);
    }

    public static class Builder extends AbstractBuilder<ExitResponse> {
        @Override
        public ExitResponse build() {
            return new ExitResponse(result, message, timestamp);
        }
    }

    @Override
    public Operation getOperation() {
        return Operation.EXIT;
    }
}
