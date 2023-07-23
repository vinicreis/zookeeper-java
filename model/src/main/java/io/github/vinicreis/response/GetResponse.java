package io.github.vinicreis.response;

import com.google.gson.annotations.SerializedName;
import io.github.vinicreis.enums.Operation;
import io.github.vinicreis.enums.Result;

/**
 * Represents a GET response made to when a {@code Node} leaves the connection with {@code Controller}
 */
public class GetResponse extends Response {
    @SerializedName("value") private final String value;
    @SerializedName("timestamp") private final Long timestamp;

    private GetResponse(Result result, String message, String value, Long timestamp) {
        super(result, message);

        this.value = value;
        this.timestamp = timestamp;
    }

    /**
     * GET response builder class
     */
    public static class Builder extends AbstractBuilder<GetResponse> {
        private String value = null;
        private Long timestamp = null;

        /**
         * Adds a value to be returned on the response.
         * @param value a {@code String} value to be added
         * @return a {@code Builder} instance to chain building
         */
        public Builder value(String value) {
            this.value = value;

            return this;
        }

        /**
         * Adds a timestamp to be returned on the response.
         * @param timestamp a {@code String} value to be added
         * @return a {@code Builder} instance to chain building
         */
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

    /**
     * Gets the value found by the {@code Server} if found.
     * @return {@code null} if the value is found and is not outdated. Otherwise,
     * a {@code String} value associated to the key
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @return {@code null} if the value is found and is not outdated. Otherwise,
     * a {@code Long} value representing the timestamp related to the key receibed
     */
    public Long getTimestamp() {
        return timestamp;
    }
}
