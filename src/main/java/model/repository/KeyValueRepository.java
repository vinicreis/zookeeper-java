package model.repository;

import javafx.util.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueRepository {
    private final Map<String, Pair<String, Long>> data = new ConcurrentHashMap<>();
}
