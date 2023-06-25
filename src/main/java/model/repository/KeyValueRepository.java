package model.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueRepository {
    private final Map<String, Entry> data = new ConcurrentHashMap<>();
    private final TimestampRepository timestampRepository;

    public KeyValueRepository(TimestampRepository timestampRepository) {
        this.timestampRepository = timestampRepository;
    }

    private static class Entry {
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
        data.putIfAbsent(key, new Entry(value, timestampRepository.getCurrent()));

        return data.get(key).getTimestamp();
    }
}
