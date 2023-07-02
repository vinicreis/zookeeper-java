package model.response;

import model.Operation;
import model.Response;
import model.Result;

public class JoinResponse extends Response {

    private JoinResponse(Result result, String message, Long timestamp) {
        super(result, message, timestamp);
    }

    public static class Builder extends AbstractBuilder<JoinResponse> {
        @Override
        public JoinResponse build() {
            return new JoinResponse(this.result, this.message, this.timestamp);
        }
    }

    @Override
    public Operation getOperation() {
        return Operation.JOIN;
    }
}
