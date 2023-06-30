package model.repository;

import model.exception.OutdatedEntryException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueRepository {
    private final Map<String, Entry> data = new ConcurrentHashMap<>();
    private final TimestampRepository timestampRepository;

    public KeyValueRepository(TimestampRepository timestampRepository) {
        this.timestampRepository = timestampRepository;
    }

    public static class Entry {
        private final String value;
        private final Long timestamp;

        private Entry(String value, Long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public String getValue() {
            return value;
        }

        public Long getTimestamp() {
            return timestamp;
        }
    }

    public Long upsert(String key, String value) {
        final Long timestamp = timestampRepository.getCurrent();

        data.put(key, new Entry(value, timestamp));

        return timestamp;
    }

    public void update(String key, String value, Long timestamp) {
        data.putIfAbsent(key, new Entry(value, timestamp));
    }

    public Entry find(String key, Long timestamp) throws OutdatedEntryException {
        final Entry result = data.getOrDefault(key, null);

        if(result == null) return null;
        if(result.getTimestamp() < timestamp)
            throw new OutdatedEntryException(key, timestampRepository.getCurrent());

        return data.get(key);
    }
}
