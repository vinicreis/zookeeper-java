package model;

import com.google.gson.annotations.SerializedName;

public abstract class Response {
    @SerializedName("response") protected Result result;
    @SerializedName("message") protected String message;
    @SerializedName("timestamp") protected Long timestamp;

    protected Response(Result result, String message, Long timestamp) {
        this.result = result;
        this.message = message;
        this.timestamp = timestamp;
    }

    public abstract static class AbstractBuilder<T> {
        protected Result result = Result.OK;
        protected String message = null;
        protected Long timestamp = null;

        public abstract T build();

        public <B extends AbstractBuilder<T>> B result(Result result) {
            this.result = result;

            return (B)this;
        }

        public <B extends AbstractBuilder<T>> B message(String message) {
            this.message = message;

            return (B)this;
        }

        public <B extends AbstractBuilder<T>> B timestamp(Long timestamp) {
            this.timestamp = timestamp;

            return (B)this;
        }

        public <B extends AbstractBuilder<T>> B exception(Exception e) {
            this.result = Result.EXCEPTION;
            this.message = "Falha ao processar operação";

            return (B)this;
        }
    }

    public abstract Operation getOperation();

    public Result getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
