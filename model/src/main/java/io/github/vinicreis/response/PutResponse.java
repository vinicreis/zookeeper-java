package io.github.vinicreis.response;

import com.google.gson.annotations.SerializedName;
import io.github.vinicreis.enums.Operation;
import io.github.vinicreis.enums.Result;

/**
 * Represents a PUT request made when a {@code Client} instance wants to insert
 * a value in a determined key.
 */
public class PutResponse extends Response {
    @SerializedName("timestamp") private final Long timestamp;

    private PutResponse(Result result, String message, Long timestamp) {
        super(result, message);

        this.timestamp = timestamp;
    }

    /**
     * PUT response builder class
     */
    public static class Builder extends AbstractBuilder<PutResponse> {
        private Long timestamp = null;

        /**
         * Adds a timestamp value to the response
         * @param timestamp timestamp value to be added
         * @return a {@code Builder} instance to chain building
         */
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

    /**
     * Gets the timestamp value in case the value is added.
     * @return {@code null} if the value add fails. Otherwise, a {@code Long}
     * value representing the timestamp associated to key received.
     */
    public Long getTimestamp() {
        return timestamp;
    }
}
