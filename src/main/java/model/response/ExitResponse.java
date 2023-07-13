package model.response;

import model.enums.Operation;
import model.enums.Result;

/**
 * Represents a EXIT response made to when a {@code Node} leaves the connection with {@code Controller}
 */
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
