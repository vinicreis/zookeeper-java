package util;

import com.google.gson.Gson;

/**
 * Implements static methods to handle serialization operation to have a default
 * serialization strategy for the whole project.
 */
public class Serializer {
    private static final Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
