package model.response;

import com.google.gson.annotations.SerializedName;
import model.enums.Operation;
import model.enums.Result;

/**
 * Generic abstract class to represent the response to any {@code Request} type.
 */
public abstract class Response {
    @SerializedName("result") protected Result result;
    @SerializedName("message") protected String message;

    protected Response(Result result, String message) {
        this.result = result;
        this.message = message;
    }

    /**
     * Abstract builder class that builds a response of type {@code T}
     * @param <T> type of response created by the class
     */
    @SuppressWarnings("unchecked")
    public abstract static class AbstractBuilder<T> {
        protected Result result = Result.OK;
        protected String message = null;

        /**
         * Builds the response.
         * @return an instance os type {@code T} as a response
         */
        public abstract T build();

        /**
         * Adds a {@code Result} value to the response
         * @param result result value to be added
         * @return the builder instance of type {@code B}
         * @param <B> Builder type to returned and resume the building
         */
        public <B extends AbstractBuilder<T>> B result(Result result) {
            this.result = result;

            return (B)this;
        }

        /**
         * Adds a {@code String} value as a message to the response
         * @param message message to be added
         * @return the builder instance of type {@code B}
         * @param <B> Builder type to returned and resume the building
         */
        public <B extends AbstractBuilder<T>> B message(String message) {
            this.message = message;

            return (B)this;
        }

        /**
         * Adds an {@code Exception} message and result to the response
         * @param e exception to read values from
         * @return the builder instance of type {@code B}
         * @param <B> Builder type to returned and resume the building
         */
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
}
