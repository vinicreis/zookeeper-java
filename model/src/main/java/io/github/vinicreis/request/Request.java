package io.github.vinicreis.request;

import com.google.gson.annotations.SerializedName;
import io.github.vinicreis.enums.Operation;

/**
 * Generic interface to represent a request between any instance type.
 */
public abstract class Request {
    @SerializedName("host") protected String host;
    @SerializedName("port") protected int port;

    protected Request(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Gets the operation requested.
     * @return Returns an {@code Operation} instance representing this request operation.
     */
    abstract public Operation getOperation();

    /**
     * Gets the sender host address of this request.
     * @return a {@code string} value containing the address.
     */
    abstract public String getHost();

    /**
     * Gets the sender port of this request.
     * @return an {@code int} value with the port
     */
    abstract public int getPort();
}
