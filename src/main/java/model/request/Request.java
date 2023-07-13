package model.request;

import model.enums.Operation;

/**
 * Generic interface to represent a request between any instance type.
 */
public interface Request {
    /**
     * Gets the operation requested.
     * @return Returns an {@code Operation} instance representing this request operation.
     */
    Operation getOperation();

    /**
     * Gets the sender host address of this request.
     * @return a {@code string} value containing the address.
     */
    String getHost();

    /**
     * Gets the sender port of this request.
     * @return an {@code int} value with the port
     */
    int getPort();
}
