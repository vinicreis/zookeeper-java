package model.response;

import com.google.gson.annotations.SerializedName;
import model.Operation;
import model.Response;
import model.Result;

public class GetResponse extends Response {
    @SerializedName("value")
    private String value;

    @SerializedName("timestamp")
    private Long timestamp;

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

    public static Builder builder() {
        return new Builder();
    }
}
