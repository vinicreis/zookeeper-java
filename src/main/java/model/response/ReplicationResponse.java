package model.response;

import com.google.gson.annotations.SerializedName;
import model.enums.Operation;
import model.enums.Result;

public class ReplicationResponse extends Response {
    @SerializedName("timestamp") private final Long timestamp;

    private ReplicationResponse(Result result, String message, Long timestamp) {
        super(result, message);

        this.timestamp = timestamp;
    }

    public static class Builder extends AbstractBuilder<ReplicationResponse> {
        private Long timestamp = null;

        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;

            return this;
        }

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
