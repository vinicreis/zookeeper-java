package model.response;

import model.enums.Operation;
import model.enums.Result;

public class JoinResponse extends Response {

    private JoinResponse(Result result, String message) {
        super(result, message);
    }

    public static class Builder extends AbstractBuilder<JoinResponse> {
        @Override
        public JoinResponse build() {
            return new JoinResponse(this.result, this.message);
        }
    }

    @Override
    public Operation getOperation() {
        return Operation.JOIN;
    }
}
