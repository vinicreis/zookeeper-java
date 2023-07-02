package model.response;

import com.google.gson.annotations.SerializedName;
import model.enums.Operation;
import model.enums.Result;

public class GetResponse extends Response {
    @SerializedName("value") private String value;

    private GetResponse(Result result, String message, String value, Long timestamp) {
        super(result, message, timestamp);

        this.value = value;
    }

    public static class Builder extends AbstractBuilder<GetResponse> {
        private String value = null;

        public Builder value(String value) {
            this.value = value;

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
}
