package model.response;

import com.google.gson.annotations.SerializedName;
import model.enums.Operation;
import model.enums.Result;

public abstract class Response {
    @SerializedName("response") protected Result result;
    @SerializedName("message") protected String message;

    protected Response(Result result, String message) {
        this.result = result;
        this.message = message;
    }

    public abstract static class AbstractBuilder<T> {
        protected Result result = Result.OK;
        protected String message = null;

        public abstract T build();

        public <B extends AbstractBuilder<T>> B result(Result result) {
            this.result = result;

            return (B)this;
        }

        public <B extends AbstractBuilder<T>> B message(String message) {
            this.message = message;

            return (B)this;
        }

        public <B extends AbstractBuilder<T>> B exception(Exception e) {
            // TODO: Do something with the exception or remove this method
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
}