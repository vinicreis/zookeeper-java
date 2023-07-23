package io.github.vinicreis.exception;

/**
 * Exception to be thrown in case a key with an outdated timestamp tries to be fetch.
 */
public class OutdatedEntryException extends Exception {
    private final String key;
    private final Long currentTimestamp;

    public OutdatedEntryException(String key, Long currentTimestamp) {
        this.key = key;
        this.currentTimestamp = currentTimestamp;
    }

    /**
     * Get the requested key with error.
     * @return the key fetched with an outdated timestamp.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the timestamp that the user has associated with this key.
     * @return the integer timestamp value
     */
    public Long getCurrentTimestamp() {
        return currentTimestamp;
    }
}
