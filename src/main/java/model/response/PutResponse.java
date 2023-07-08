package model.response;

import com.google.gson.annotations.SerializedName;
import model.enums.Operation;
import model.enums.Result;

public class PutResponse extends Response {
    @SerializedName("timestamp") private final Long timestamp;

    private PutResponse(Result result, String message, Long timestamp) {
        super(result, message);

        this.timestamp = timestamp;
    }

    public static class Builder extends AbstractBuilder<PutResponse> {
        private Long timestamp = null;

        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;

            return this;
        }

        @Override
        public PutResponse build() {
            return new PutResponse(result, message, timestamp);
        }
    }

    @Override
    public Operation getOperation() {
        return Operation.PUT;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
