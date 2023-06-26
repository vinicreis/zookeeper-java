package model.request;

import model.response.GetResponse;

public class GetRequest {
    private final String key;
    private final Long timestamp;

    public GetRequest(String key, Long timestamp) {
        this.key = key;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
