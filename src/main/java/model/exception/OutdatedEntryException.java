package model.exception;

public class OutdatedEntryException extends Exception {
    private final String key;
    private final Long currentTimestamp;

    public OutdatedEntryException(String key, Long currentTimestamp) {
        this.key = key;
        this.currentTimestamp = currentTimestamp;
    }

    public String getKey() {
        return key;
    }

    public Long getCurrentTimestamp() {
        return currentTimestamp;
    }
}
