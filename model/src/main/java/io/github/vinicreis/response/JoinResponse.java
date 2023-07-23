package io.github.vinicreis.response;

import io.github.vinicreis.enums.Result;
import io.github.vinicreis.enums.Operation;

/**
 * Represents a JOIN response to when a {@code Node} instance to JOIN the Controller environment.
 */
public class JoinResponse extends Response {

    private JoinResponse(Result result, String message) {
        super(result, message);
    }

    /**
     * JOIN response builder class
     */
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
