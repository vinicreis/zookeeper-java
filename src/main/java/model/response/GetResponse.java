package model.response;

import com.google.gson.annotations.SerializedName;
import model.enums.Operation;
import model.enums.Result;

public class GetResponse extends Response {
    @SerializedName("value") private final String value;
    @SerializedName("timestamp") private final Long timestamp;

    private GetResponse(Result result, String message, String value, Long timestamp) {
        super(result, message);

        this.value = value;
        this.timestamp = timestamp;
    }

    public static class Builder extends AbstractBuilder<GetResponse> {
        private String value = null;
        private Long timestamp = null;

        public Builder value(String value) {
            this.value = value;

            return this;
        }

        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;

            return this;
        }

        @Override
        public GetResponse build() {
            return new GetResponse(result, message, value, timestamp);
        }
    }

    @Override
    public Operation getOperation() {
        return Operation.GET;
    }

    public String getValue() {
        return value;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
