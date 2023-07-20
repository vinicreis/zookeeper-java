package model.repository;

import model.exception.OutdatedEntryException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static util.AssertionUtils.check;

/**
 * A key/value repository to store key/value pairs with an internal timestamp.
 */
public class KeyValueRepository {
    private final Map<String, Entry> data = new ConcurrentHashMap<>();
    private final TimestampRepository timestampRepository;

    /**
     * Constructor to be used on controller servers, which will have an instance of a timestamp repository to
     * manage local timestamps values.
     * @see server.Controller
     * @see TimestampRepository
     * @param timestampRepository timestamp repository instance
     */
    public KeyValueRepository(TimestampRepository timestampRepository) {
        this.timestampRepository = timestampRepository;
    }

    /**
     * Constructor to be used on nodesm where the key/value pairs a replicated to with the passes timestamp.
     * @see TimestampRepository
     * @see server.Controller
     */
    public KeyValueRepository() {
        this.timestampRepository = null;
    }

    /**
     * Entry class that holds all this repository data. Similar to a @{code Tuple} with types {@code String},
     * and {@code Long}.
     */
    public static class Entry {
        private final String value;
        private final Long timestamp;

        /**
         * Default constructor.
         */
        private Entry(String value, Long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        /**
         * Gets the entry value.
         * @return gets the read value from server.
         */
        public String getValue() {
            return value;
        }

        /**
         * Get the timestamp returned from host (at least now)...
         * @return returns a timestamp timestamp unit to avoid errors.
         */
        public Long getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Insert a value using the key property.
     * @param key key to be inserted on host.
     * @param value value to be added to the server
     * @return a {@code timestamp} a timestamp that indicates the correct time the controller server added the key
     * to repository.
     */
    public Long insert(String key, String value) {
        check(timestampRepository != null, "Timestamp repository is not initialized!");

        final Long timestamp = timestampRepository.getCurrent();

        data.put(key, new Entry(value, timestamp));

        return timestamp;
    }

    /**
     * Replicate data into this instance from controller server.
     * @param key key to be replicated to
     * @param value value to be replicated
     * @param timestamp associated timestamp to be replicated
     */
    public void replicate(String key, String value, Long timestamp) {
        data.put(key, new Entry(value, timestamp));
    }

    /**
     * Finds a key according to last associated timestamp saved on client.
     * @param key key to be found
     * @param timestamp timestamp associated with this key by the client
     * @return the value found by this key
     * @throws OutdatedEntryException if a value is found where the client timestamp is greater
     * than or equal the host one.
     */
    public Entry find(String key, Long timestamp) throws OutdatedEntryException {
        final Entry result = data.getOrDefault(key, null);

        if(timestamp != null && timestamp > 0 && result != null && result.getTimestamp() < timestamp)
            throw new OutdatedEntryException(key, result.getTimestamp());

        return result;
    }
}
