package model.response;

import model.enums.Operation;
import model.enums.Result;

public class ExitResponse extends Response {
    private ExitResponse(Result result, String message) {
        super(result, message);
    }

    public static class Builder extends AbstractBuilder<ExitResponse> {
        @Override
        public ExitResponse build() {
            return new ExitResponse(result, message);
        }
    }

    @Override
    public Operation getOperation() {
        return Operation.EXIT;
    }
}
