package model.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueRepository {
    private final Map<String, String> data = new ConcurrentHashMap<>();
}
